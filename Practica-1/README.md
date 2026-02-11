# 🛠️ Práctica 1: Configuración del Entorno JEE

## 📄 Descripción
Esta práctica consiste en la preparación, instalación y configuración del entorno de desarrollo necesario para la asignatura, enfocado en tecnologías **Java Enterprise Edition (JEE)**.

El objetivo principal es disponer de un entorno funcional con los servidores de referencia y las herramientas de construcción integradas en el IDE para poder trabar en el durante el resto de prácticas.

## 📦 Software Instalado
Siguiendo las especificaciones de la asignatura, se ha configurado el siguiente stack tecnológico:

| Herramienta | Versión Configurada | Función |
| :--- | :--- | :--- |
| **Java JDK** | 1.8.0_361 | Kit de desarrollo Java SE |
| **Eclipse IDE** | 2022-12 R | Entorno de desarrollo integrado (Web Developers) |
| **Apache Tomcat** | 10.0.27 | Contenedor de Servlets/JSP |
| **GlassFish** | 5.1.0 | Servidor de aplicaciones JEE completo |
| **Apache Maven** | 3.8.7 | Gestión de dependencias y construcción |
| **Apache Ant** | 1.10.13 | Automatización de tareas de compilación |

## ⚙️ Detalles de la Configuración
Se han realizado las siguientes tareas de configuración del sistema:

1.  **Variables de Entorno:** Configuración de `JAVA_HOME`, `ANT_HOME`, `M2_HOME`, `CATALINA_HOME` y `GLASSFISH_HOME` en el PATH del sistema.
2.  **Integración en Eclipse:**
    * Instalación de las **GlassFish Tools** desde el repositorio de Oracle (`oepe`) para permitir la gestión del servidor desde el IDE.
    * Vinculación de los servidores Tomcat y GlassFish dentro de la vista "Servers" de Eclipse.
3.  **Verificación:** Comprobación de arranque de servidores en `http://localhost:8080`.

## 📂 Entregable
En este directorio se adjunta el documento **Word (.docx) y PDF** que contiene:
* Paso a paso de la descarga e instalación.
* Capturas de pantalla de las consolas mostrando las versiones instaladas (`-version`).
* Pruebas de funcionamiento de los servidores locales.
* Evidencias de la configuración de variables de entorno.
