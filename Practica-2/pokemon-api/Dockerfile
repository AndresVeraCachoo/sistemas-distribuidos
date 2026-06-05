FROM python:3.12-slim

WORKDIR /app

# Evita que Python escriba archivos .pyc en el disco duro
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV PYTHONPATH=/app

# Copiar y cachear dependencias primero
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el código fuente después
COPY src/ ./src/
ENV PYTHONPATH=/app/src

EXPOSE 5000

CMD ["python", "src/app.py"]