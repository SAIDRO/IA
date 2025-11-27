import cv2
import numpy as np
import os
import easyocr

class ProcesadorTextoIA:
    def __init__(self, output_folder="temp_segmentos"):
        self.output_folder = output_folder
        os.makedirs(self.output_folder, exist_ok=True)
        
        # Inicializamos EasyOCR
        self.reader = easyocr.Reader(['es'], gpu=False, verbose=False)

    def _engrosar_tinta(self, img):
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        kernel = np.ones((2, 2), np.uint8)
        return cv2.erode(gray, kernel, iterations=1)

    def _fusionar_cajas_cercanas(self, cajas_raw):
        if not cajas_raw: return []

        # Normalizar cajas a [x, y, w, h]
        cajas_rect = []
        for item in cajas_raw:
            bbox = item[0] 
            puntos = np.array(bbox, dtype=np.int32)
            x = np.min(puntos[:, 0])
            y = np.min(puntos[:, 1])
            w = np.max(puntos[:, 0]) - x
            h = np.max(puntos[:, 1]) - y
            cajas_rect.append([x, y, w, h])

        # Ordenar por Y
        cajas_rect.sort(key=lambda b: b[1])

        cajas_finales = []
        caja_actual = cajas_rect[0]

        for i in range(1, len(cajas_rect)):
            sig_caja = cajas_rect[i]
            x1, y1, w1, h1 = caja_actual
            x2, y2, w2, h2 = sig_caja

            # Lógica de fusión
            interseccion_x = max(0, min(x1+w1, x2+w2) - max(x1, x2))
            interseccion_y = max(0, min(y1+h1, y2+h2) - max(y1, y2))
            area_inter = interseccion_x * interseccion_y
            area_min = min(w1*h1, w2*h2)
            
            es_duplicado = area_inter > (area_min * 0.5)

            mismo_renglon = abs(y1 - y2) < (h1 * 0.4)
            distancia_x = x2 - (x1 + w1)
            cerca_horizontal = distancia_x < (h1 * 1.5)

            if es_duplicado or (mismo_renglon and cerca_horizontal):
                nuevo_x = min(x1, x2)
                nuevo_y = min(y1, y2)
                nuevo_w = max(x1+w1, x2+w2) - nuevo_x
                nuevo_h = max(y1+h1, y2+h2) - nuevo_y
                caja_actual = [nuevo_x, nuevo_y, nuevo_w, nuevo_h]
            else:
                cajas_finales.append(caja_actual)
                caja_actual = sig_caja
        
        cajas_finales.append(caja_actual)
        return cajas_finales

    def procesar_imagen(self, ruta_imagen):
        if not os.path.exists(ruta_imagen): return False
        
        img_original = cv2.imread(ruta_imagen)
        if img_original is None: return False
        h_img, w_img = img_original.shape[:2]

        # Pre-proceso ligero
        img_detect = self._engrosar_tinta(img_original)
        
        resultados_totales = []

        # --- PASADA 1---
        try:
            res_big = self.reader.readtext(
                img_detect, paragraph=False, 
                mag_ratio=0.8, 
                text_threshold=0.4,
                width_ths=0.1 
            )
            resultados_totales.extend(res_big)
        except: pass

        # --- PASADA 2: VISIÓN DE DETALLE ---
        try:
            res_small = self.reader.readtext(
                img_detect, paragraph=False, 
                mag_ratio=1.5,
                text_threshold=0.4
            )
            resultados_totales.extend(res_small)
        except: pass

        if not resultados_totales:
            print("No se detectó texto.")
            return False

        cajas_finales = self._fusionar_cajas_cercanas(resultados_totales)

        # Limpieza
        for f in os.listdir(self.output_folder):
            if f.startswith("palabra_"): os.remove(os.path.join(self.output_folder, f))

        count = 0
        pad = 12

        # Recorte
        for (x, y, w, h) in cajas_finales:
            if w < 10 or h < 10: continue

            y1 = int(max(0, y - pad))
            y2 = int(min(h_img, y + h + pad))
            x1 = int(max(0, x - pad))
            x2 = int(min(w_img, x + w + pad))

            roi = img_original[y1:y2, x1:x2]
            
            filename = f"palabra_{count:04d}.jpg"
            cv2.imwrite(os.path.join(self.output_folder, filename), roi)
            count += 1

        print(f"ÉXITO: {count} elementos detectados.")
        return True