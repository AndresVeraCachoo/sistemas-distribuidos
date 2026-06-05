import os
import json
import time
import smtplib
import pika
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

# Configuracion de RabbitMQ y Gmail desde las variables de entorno
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "rabbitmq")
EMAIL_SENDER = os.getenv("EMAIL_SENDER")
EMAIL_PASSWORD = os.getenv("EMAIL_PASSWORD")

def enviar_correo(destinatario, asunto, cuerpo_html):
    if not EMAIL_SENDER or not EMAIL_PASSWORD:
        print("Error: Credenciales de correo no configuradas en el .env")
        return

    msg = MIMEMultipart("alternative")
    msg["Subject"] = asunto
    msg["From"] = EMAIL_SENDER
    msg["To"] = destinatario

    parte_html = MIMEText(cuerpo_html, "html")
    msg.attach(parte_html)

    try:
        # Conexión al servidor de Google
        server = smtplib.SMTP("smtp.gmail.com", 587)
        server.starttls()
        server.login(EMAIL_SENDER, EMAIL_PASSWORD)
        server.sendmail(EMAIL_SENDER, destinatario, msg.as_string())
        server.quit()
        print(f"Correo enviado exitosamente a {destinatario}")
    except Exception as e:
        print(f"Error al enviar correo a {destinatario}: {e}")

def procesar_mensaje(ch, method, properties, body):
    """Esta función se ejecuta automáticamente cada vez que Java manda algo a RabbitMQ"""
    try:
        data = json.loads(body)
        action = data.get("action")
        email = data.get("email")
        nombre = data.get("nombre")

        print(f"Procesando evento de RabbitMQ: {action} para {email}")

        if action == "welcome":
            asunto = "¡Bienvenido a PokeApp, Entrenador!"
            html = f"""
            <html>
                <body style="font-family: Arial, sans-serif; text-align: center; background-color: #f4f4f4; padding: 20px;">
                    <div style="background-color: white; padding: 30px; border-radius: 10px; max-width: 500px; margin: auto; border-top: 5px solid #FF0000;">
                        <h2 style="color: #333;">¡Hola, {nombre}!</h2>
                        <p style="color: #555; font-size: 16px;">Tu viaje Pokémon está a punto de comenzar. Te damos la bienvenida oficial a la PokeApp.</p>
                        <p style="color: #555; font-size: 16px;">Aquí podrás buscar, registrar y gestionar tu equipo perfecto.</p>
                        <br>
                        <p style="font-weight: bold; color: #333;">¡Hazte con todos!</p>
                    </div>
                </body>
            </html>
            """
            enviar_correo(email, asunto, html)

        elif action == "goodbye":
            asunto = "Tu aventura ha terminado (PokeApp)"
            html = f"""
            <html>
                <body style="font-family: Arial, sans-serif; text-align: center; background-color: #f4f4f4; padding: 20px;">
                    <div style="background-color: white; padding: 30px; border-radius: 10px; max-width: 500px; margin: auto; border-top: 5px solid #555555;">
                        <h2 style="color: #333;">Hasta pronto, {nombre}</h2>
                        <p style="color: #555; font-size: 16px;">Hemos eliminado tu cuenta y tu historial de búsquedas de nuestros servidores.</p>
                        <p style="color: #555; font-size: 16px;">Esperamos volver a verte por la región pronto.</p>
                    </div>
                </body>
            </html>
            """
            enviar_correo(email, asunto, html)
            
    except Exception as e:
        print(f"Error procesando el mensaje de la cola: {str(e)}")

def iniciar_worker():
    # Retardo inicial para dar tiempo a que RabbitMQ arranque completamente en Docker
    print("Iniciando worker de correos, esperando a RabbitMQ...")
    time.sleep(10)
    
    conexion = None
    intentos = 5
    while intentos > 0:
        try:
            conexion = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
            break
        except Exception:
            print(f"🔄 RabbitMQ no está listo. Reintentando en 5 segundos... ({intentos} intentos restantes)")
            time.sleep(5)
            intentos -= 1

    if not conexion:
        print("No se pudo conectar a RabbitMQ. El worker se detendrá.")
        return

    canal = conexion.channel()
    # Nos aseguramos de que la cola exista (durable=True para que no se borre si se reinicia)
    canal.queue_declare(queue="email_queue", durable=True)

    # Nos ponemos a escuchar
    canal.basic_consume(queue="email_queue", on_message_callback=procesar_mensaje, auto_ack=True)
    
    print("🚀 Worker de correos iniciado y esperando mensajes de Java...")
    canal.start_consuming()

if __name__ == "__main__":
    iniciar_worker()