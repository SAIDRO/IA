import os
import re
from openai import OpenAI

class EditorInteligente:
    def __init__(self):
        print(f"Iniciando correccion ")
        
        # --- CONFIGURACIÓN ---       
        self.api_key = "TU_API_KEY" 
        self.base_url = "https://api.groq.com/openai/v1"
        self.model_name = "llama-3.3-70b-versatile"
        self.client = None
        self.client = OpenAI(api_key=self.api_key, base_url=self.base_url)
              

    def _limpieza_regex(self, texto):
        """Limpieza básica"""
        if not texto: return ""
        texto = texto.replace("\n", " ").strip()
        texto = re.sub(r'[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ.,;:\-\s"\¿\?¡\!]', '', texto)
        return texto

    def procesar_texto(self, texto_crudo):
        if not texto_crudo or len(texto_crudo) < 2:
            return ""

        texto_limpio = self._limpieza_regex(texto_crudo)
        print(f"Entrada OCR: {texto_limpio}")

        if not self.client:
            print("Sin API configurada")
            return texto_limpio


        # --- PROMPT---
        system_prompt = (
            "Eres un experto editor de textos digitalizados. Tu tarea es CORREGIR errores de OCR "
            "en textos manuscritos en español.\n"
            "Reglas:\n"
            "1. Corrige ortografía, acentos y gramática.\n"
            "2. Une letras separadas injustificadamente (ej: 'H o l a' -> 'Hola').\n"
            "3. NO inventes texto. Solo repara lo que ves.\n"
            "4. Devuelve SOLAMENTE el texto corregido, sin introducciones ni comillas."
        )
        
        user_prompt = f"Texto OCR dañado: {texto_limpio}\n\nTexto corregido:"

        try:
            response = self.client.chat.completions.create(
                model=self.model_name,
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": user_prompt}
                ],
                temperature=0.2,
                max_tokens=1024
            )
            
            resultado = response.choices[0].message.content.strip()
            if "Texto corregido:" in resultado:
                resultado = resultado.replace("Texto corregido:", "").strip()
            
            print(f" [IA ({self.model_name})] {resultado}")
            return resultado

        except Exception as e:
            print(f"Error en API: {e}")
            return texto_limpio