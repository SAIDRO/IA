import torch
from transformers import VitsModel, AutoTokenizer
import scipy.io.wavfile
import os

class LocutorNeuronal:
    def __init__(self):
        self.device = 'cpu'
        print(f"Cargando MMS-TTS (Español)")
        
        self.model_name = "facebook/mms-tts-spa"
        
        try:
            self.tokenizer = AutoTokenizer.from_pretrained(self.model_name)
            self.model = VitsModel.from_pretrained(self.model_name).to(self.device)
            print("Motor cargado.")
        except Exception as e:
            print(f"Error Voz: {e}")

    def generar_audio_archivo(self, text, output_filename):
        if not text: return False
        
        print(f"Sintetizando")
        
        try:
            inputs = self.tokenizer(text, return_tensors="pt")
            inputs = inputs.to(self.device)

            with torch.no_grad():
                output = self.model(**inputs).waveform

            waveform = output.cpu().float().numpy()
            
            scipy.io.wavfile.write(
                output_filename, 
                rate=self.model.config.sampling_rate, 
                data=waveform.T
            )
            return os.path.exists(output_filename)
        except Exception as e:
            print(f"Error generando audio: {e}")
            return False