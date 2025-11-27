import streamlit as st
import os
from PIL import Image
from procesador_texto import ProcesadorTextoIA
from reconocedor import CerebroLector
from post_procesador import EditorInteligente
from sintetizador_voz import LocutorNeuronal


# --- CONFIGURACIÓN DE LA PÁGINA ---
st.set_page_config(page_title="Reconocimiento de texto manuscrito a Voz", layout="centered")

# --- GESTIÓN DE CARPETAS TEMPORALES ---
DIRS = ["temp_imagenes", "temp_segmentos", "temp_audio"]
for d in DIRS:
    os.makedirs(d, exist_ok=True)

# --- ESTILOS CSS ---
st.markdown("""
    <style>
    .stButton>button { width: 100%; background-color: #764ba2; color: white; border-radius: 8px; font-weight: bold; }
    .stProgress > div > div > div > div { background-color: #764ba2; }
    </style>
    """, unsafe_allow_html=True)

# --- SIDEBAR ---
st.title("Lector de Manuscritos ")

with st.sidebar:
    st.header("Panel de Control")
    st.success("""
    1. **Detector:** EasyOCR (Binarización Adaptativa)
    2. **HTR:** TrOCR Large (Microsoft)
    3. **Corrector:** Llama 3.3
    4. **Voz:** MMS-TTS (Facebook)
    """)
    
    if st.button("Limpiar Archivos Temporales"):
        for d in DIRS:
            for f in os.listdir(d):
                try: os.remove(os.path.join(d, f))
                except: pass
        st.toast("Memoria limpia")

# --- MAIN ---
uploaded_file = st.file_uploader("Subir imagen", type=["jpg", "png", "jpeg"])

if uploaded_file:
    # Guardar PNG
    ruta_img_input = os.path.join("temp_imagenes", "input.jpg")
    img_pil = Image.open(uploaded_file)
    if img_pil.mode == 'RGBA': img_pil = img_pil.convert('RGB')
    img_pil.save(ruta_img_input)
    st.image(img_pil, caption="Imagen Original", use_container_width=True)


    if st.button("INICIAR PROCESAMIENTO"):
        progreso = st.progress(0)
        estado = st.empty()

        try:
            # CARGA DE MODELOS
            estado.text("Cargando modelos")
            procesador = ProcesadorTextoIA(output_folder="temp_segmentos")
            lector = CerebroLector()
            editor = EditorInteligente()
            locutor = LocutorNeuronal()

            # --- FASE 1: VISIÓN ---
            estado.markdown("#### Fase 1: Detección y segmentación")
            if not procesador.procesar_imagen(ruta_img_input):
                st.error("No se detectaron palabras.")
                st.stop()
            
            # Mostrar Debug Vision
            debug_path = os.path.join("temp_segmentos", "debug_vision.jpg")
            if os.path.exists(debug_path):
                st.image(debug_path, caption="Visión de la IA", use_container_width=True)

            # Mostrar Palabras
            palabras = sorted([f for f in os.listdir("temp_segmentos") if f.startswith("palabra_")])
            if palabras:
                st.caption(f"Palabras detectadas: {len(palabras)}")
                cols = st.columns(6)
                for i, p in enumerate(palabras[:6]):
                    with cols[i]: st.image(os.path.join("temp_segmentos", p))
            
            progreso.progress(30)

            # --- FASE 2: HTR ---
            estado.markdown("#### Fase 2: Reconocimiento optico (TrOCR)")
            texto_crudo = lector.procesar_carpeta("temp_segmentos")
            st.code(texto_crudo if texto_crudo else "Sin texto")
            progreso.progress(60)

            # --- FASE 3: CORRECCIÓN ---
            estado.markdown("#### Fase 3: Corrección contextual")
            texto_final = editor.procesar_texto(texto_crudo)
            st.success("Texto final corregido:")
            st.write(f"> {texto_final}")
            progreso.progress(80)

            # --- FASE 4: VOZ ---
            estado.markdown("#### Fase 4: Síntesis de audio...")
            ruta_audio = os.path.join("temp_audio", "audio_final.wav")
            if os.path.exists(ruta_audio): os.remove(ruta_audio)

            if locutor.generar_audio_archivo(texto_final, ruta_audio):
                st.audio(ruta_audio, format="audio/wav")
                with open(ruta_audio, "rb") as f:
                    st.download_button("Descargar Audio", f, file_name="lectura_ia.wav")
                estado.success("¡Completado!")
                progreso.progress(100)
            else:
                st.warning("Error generando audio.")

        except Exception as e:
            st.error(f"Error crítico: {e}")