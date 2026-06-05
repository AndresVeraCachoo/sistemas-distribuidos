# 🛠️ Ejercicio Subida de Nota: Relojes Vectoriales y Cortes Consistentes

### 📄 Descripción

Esta práctica, diseñada para subir nota en la asignatura de Sistemas Distribuidos , consiste en la implementación de un programa en Python para simular y analizar estados globales en sistemas asíncronos. 

El objetivo principal es procesar un flujo de eventos para construir una representación matricial de **Relojes Vectoriales** mediante el algoritmo de Chandy y Lamport. Esto permite identificar con total precisión si existe una relación de **causalidad** ($V_1 < V_2$) o **concurrencia** ($V(x) < V(y) \text{ o } V(y) < V(x)$ falso) entre cualquier par de eventos. Adicionalmente, el proyecto incluye una validación algorítmica para determinar si diferentes **cortes globales** son consistentes o inconsistentes en función del estado de los mensajes en red.

### 📦 Software Instalado

Dado que se trata de un entorno de ejecución de scripts lógicos, el stack tecnológico es el siguiente:

| Herramienta | Versión Configurada | Función |
| :--- | :--- | :--- |
| **Python** | 3.8 o superior | Intérprete base para la ejecución del algoritmo de simulación. |
| **IDE / Editor** | VS Code / PyCharm / Eclipse | Entorno de desarrollo para la edición y refactorización del código. |
| **Terminal / CLI** | PowerShell / Bash / CMD | Consola interactiva para visualizar el renderizado matricial de salida. |
| **Git** (Opcional) | 2.x | Control de versiones (commits estructurados para reflejar refactorizaciones). |

### ⚙️ Detalles de la Configuración y Ejecución

El código ha sido estructurado siguiendo principios de *Clean Code* (complejidad cognitiva < 15) y se han implementado las siguientes lógicas:

* **Cálculo Dinámico de Tiempos:** El script soporta matrices de entrada de tamaño dinámico ($N$ procesos por $M$ instantes de tiempo $T$). Incrementa los relojes vectoriales locales y aplica el algoritmo de máximo vectorial en las recepciones.
* **Formato Matricial Horizontal:** Se ha programado una salida por consola que imprime la tabla temporal exactamente igual que en las especificaciones gráficas .
* **Análisis de Relaciones:** Se evalúa la condición estricta $V_1 < V_2$ entre todos los pares posibles para generar listas exhaustivas de dependencias causales y concurrentes.
* **Motor de Cortes:** Implementación del concepto de corte consistente. El algoritmo evalúa tanto cortes simétricos ($T=x$ para todos) como asimétricos (tiempos independientes por nodo), comprobando que no quede ninguna recepción "dentro" del corte cuyo envío quede "fuera".
* **Verificación:** Para ejecutar el proyecto y lanzar la prueba con la matriz del enunciado, ejecutar el siguiente comando en la raíz del directorio:
  `python SubidaNota_AndresVeraCacho.py`

### 📂 Entregable

En este directorio se adjunta el código fuente y las evidencias de resolución:
* El script principal **`SubidaNota_AndresVeraCacho.py`** (Versión 4.0) que contiene toda la estructura algorítmica comentada.