# 🛠️ Práctica 1: Configuración del Entorno JEE

## 📄 Descripción
Esta práctica consiste en la preparación, instalación y configuración del entorno de desarrollo necesario para la asignatura, enfocado en tecnologías **Java Enterprise Edition (JEE)**.

El objetivo principal es disponer de un entorno funcional con los servidores de referencia y las herramientas de construcción integradas en el IDE para poder trabar en el durante el resto de prácticas.

## 📦 Software Instalado
Siguiendo las especificaciones de la asignatura, se ha configurado el siguiente stack tecnológico:

| Herramienta | Versión Configurada | Función |
| :--- | :--- | :--- |
| **Java JDK** | 1.8.0_361 | [cite_start]Kit de desarrollo Java SE [cite: 9] |
| **Eclipse IDE** | 2022-12 R | [cite_start]Entorno de desarrollo integrado (Web Developers) [cite: 9] |
| **Apache Tomcat** | 10.0.27 | [cite_start]Contenedor de Servlets/JSP [cite: 9] |
| **GlassFish** | 5.1.0 | [cite_start]Servidor de aplicaciones JEE completo [cite: 9] |
| **Apache Maven** | 3.8.7 | [cite_start]Gestión de dependencias y construcción [cite: 9] |
| **Apache Ant** | 1.10.13 | [cite_start]Automatización de tareas de compilación [cite: 9] |

## ⚙️ Detalles de la Configuración
Se han realizado las siguientes tareas de configuración del sistema:

1.  **Variables de Entorno:** Configuración de `JAVA_HOME`, `ANT_HOME`, `M2_HOME`, `CATALINA_HOME` y `GLASSFISH_HOME` en el PATH del sistema[cite: 24, 35, 44, 51, 62].
2.  **Integración en Eclipse:**
    * Instalación de las **GlassFish Tools** desde el repositorio de Oracle (`oepe`) para permitir la gestión del servidor desde el IDE[cite: 104, 151].
    * Vinculación de los servidores Tomcat y GlassFish dentro de la vista "Servers" de Eclipse[cite: 184, 292].
3.  **Verificación:** Comprobación de arranque de servidores en `http://localhost:8080`[cite: 57, 68].

## 📂 Entregable
En este directorio se adjunta el documento **Word (.docx) y PDF** que contiene:
* Paso a paso de la descarga e instalación.
* Capturas de pantalla de las consolas mostrando las versiones instaladas (`-version`).
* Pruebas de funcionamiento de los servidores locales.
* Evidencias de la configuración de variables de entorno.
