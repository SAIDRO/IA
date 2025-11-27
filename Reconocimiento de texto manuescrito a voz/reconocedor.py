import os
import torch
from transformers import TrOCRProcessor, VisionEncoderDecoderModel, logging
from PIL import Image, ImageEnhance, ImageOps

logging.set_verbosity_error()

class CerebroLector:
    def __init__(self):
        self.device = 'cpu'
        print(f"[Iniciando Reconocedor de texto manuscrito a voz: {self.device.upper()}...")

        self.model_name = 'microsoft/trocr-large-handwritten'
        
        try:
            self.processor = TrOCRProcessor.from_pretrained(self.model_name, use_fast=True)
            self.model = VisionEncoderDecoderModel.from_pretrained(self.model_name).to(self.device)

            self.model.config.decoder_start_token_id = self.processor.tokenizer.cls_token_id
            self.model.config.pad_token_id = self.processor.tokenizer.pad_token_id
            self.model.config.vocab_size = self.model.config.decoder.vocab_size
            
            print("Modelo cargado")
        except Exception as e:
            print(f"Error cargando HTR: {e}")

    def _mejorar_imagen(self, image):
        """Aplicar filtros para resaltar la escritura."""
        enhancer = ImageEnhance.Contrast(image)
        image = enhancer.enhance(2.0) 
        enhancer = ImageEnhance.Sharpness(image)
        image = enhancer.enhance(1.5) 
        
        # Convertimos a RGB 
        if image.mode != 'RGB':
            image = image.convert('RGB')
            
        return image

    def leer_recorte(self, ruta_imagen):
        if not os.path.exists(ruta_imagen): return ""

        try:
            # Cargar imagen
            image = Image.open(ruta_imagen).convert("RGB")
            image = self._mejorar_imagen(image)
            
            pixel_values = self.processor(images=image, return_tensors="pt").pixel_values.to(self.device)
            
            generated_ids = self.model.generate(
                pixel_values,
                max_new_tokens=40,      
                num_beams=7,            
                no_repeat_ngram_size=2, 
                early_stopping=True
            )
            
            texto = self.processor.batch_decode(generated_ids, skip_special_tokens=True)[0]
            
            # Limpieza básica post-lectura
            texto = texto.strip()
            if texto.lower() in ['.', ',', '-', ';', ':']: 
                return "" 
                
            return texto
        except Exception as e:
            return ""

    def procesar_carpeta(self, carpeta_entrada):
        """ Lee secuencialmente las imágenes de la carpeta. """
        if not os.path.exists(carpeta_entrada): return ""

        # Filtrar solo imágenes de palabras
        archivos = [f for f in os.listdir(carpeta_entrada) if f.startswith("palabra_") and f.endswith(".jpg")]
        archivos.sort() 

        palabras_leidas = []
        print(f"Analizando {len(archivos)} recortes")
        
        for archivo in archivos:
            ruta = os.path.join(carpeta_entrada, archivo)
            texto = self.leer_recorte(ruta)
            if texto:
                palabras_leidas.append(texto)
                print(f"   -> {archivo}: {texto}") 

        resultado = " ".join(palabras_leidas)
        return resultado