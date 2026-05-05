# ⚡ PokeApp - Sistema Distribuido (Spring Boot & Python Flask)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=AndresVeraCachoo_sistemas-distribuidos&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=AndresVeraCachoo_sistemas-distribuidos)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AndresVeraCachoo_sistemas-distribuidos&metric=coverage)](https://sonarcloud.io/summary/new_code?id=AndresVeraCachoo_sistemas-distribuidos)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=AndresVeraCachoo_sistemas-distribuidos&metric=bugs)](https://sonarcloud.io/summary/new_code?id=AndresVeraCachoo_sistemas-distribuidos)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=AndresVeraCachoo_sistemas-distribuidos&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=AndresVeraCachoo_sistemas-distribuidos)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=AndresVeraCachoo_sistemas-distribuidos&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=AndresVeraCachoo_sistemas-distribuidos)

Este proyecto corresponde a la **Práctica Obligatoria 2**. Consiste en un sistema distribuido que integra un Frontend en Java (Spring Boot) y una API de backend en Python (Flask), comunicándose con una base de datos PostgreSQL y consumiendo servicios de un API de terceros (PokéAPI).

## 🚀 Funcionalidades Principales

La aplicación cumple con todos los requisitos funcionales exigidos en el enunciado:
1. **Pantalla de Login:** Sistema de autenticación de usuarios con conexión a base de datos.
2. **Página Principal (Buscador):** Interfaz para buscar Pokémon por nombre consumiendo la API de Python.
3. **Sistema de Batalla:** Comparador de estadísticas base entre dos Pokémon elegidos por el usuario.
4. **Perfil e Historial:** Registro de las búsquedas y batallas del usuario autenticado.

---

## 💎 Calidad de Código, Tests y CI/CD 

Este proyecto no solo se enfoca en que "funcione", sino en que sea robusto, mantenible y escalable, aplicando metodologías de nivel profesional:

* 🧹 **Análisis de Código (Sonar):** Todo el código fuente ha sido revisado y refactorizado solucionando las advertencias y code smells detectados por **SonarLint/SonarQube**, garantizando un código limpio e inmune a fugas de memoria.
* 🧪 **Tests Unitarios:**
  * **Java (Frontend):** Pruebas unitarias ejecutadas mediante Maven (`mvn clean test`) comprobando la lógica de los modelos y controladores.
  * **Python (Backend):** Tests unitarios implementados con `pytest` para validar la lógica de acceso a datos y servicios.
* 🤖 **Integración Continua (GitHub Actions):** Se ha diseñado un pipeline automatizado (`.github/workflows`) que, ante cada `git push`, levanta toda la infraestructura en contenedores Docker y ejecuta automáticamente una **colección de 8 tests E2E de Postman usando Newman**. Solo si los tests pasan, el proceso se da por válido.
* 📬 **Simulación con Postman:** La carpeta `/postman` incluye la colección oficial de pruebas que simulan tanto flujos de éxito como inyecciones de error para comprobar la tolerancia a fallos del sistema.

---

## 🛡️ Arquitectura de Tolerancia a Fallos (Manejo de Excepciones)

Uno de los pilares del proyecto es el manejo estricto de excepciones. El sistema está diseñado para que **ningún error rompa la aplicación**. En su lugar, las excepciones son capturadas, controladas y enviadas al frontend traducidas a un lenguaje amigable para el usuario.

### 🐍 Excepciones en el API de Python 
El API de Flask simula y captura activamente los siguientes escenarios críticos:

1. **Excepciones de lectura y apertura de archivos:**
   * `FileNotFoundError` / `IOError`: Capturados al intentar leer configuraciones o archivos locales inexistentes.
2. **Excepciones de acceso a Base de Datos:**
   * `psycopg2.OperationalError`: Capturado si la base de datos PostgreSQL se cae o rechaza la conexión.
   * `IntegrityError`: Capturado ante intentos de duplicar datos o violar restricciones del esquema.
3. **Excepciones de llamadas a APIs de Terceros (PokeAPI):**
   * `requests.exceptions.HTTPError`: Si el Pokémon introducido no existe (Error 404).
   * `requests.exceptions.Timeout`: Si la PokéAPI tarda demasiado en responder.
   * `requests.exceptions.ConnectionError`: Si el contenedor de Python pierde la conexión a internet.

### ☕ Excepciones en Spring Boot (Frontend)
El servidor Java actúa como un escudo protector para el usuario mediante un `@ControllerAdvice` (`GlobalExceptionHandler.java`), gestionando:

* `HttpClientErrorException`: Traduce los errores 404 de Python para mostrar en pantalla *"El Pokémon introducido no existe"*.
* `ResourceAccessException`: Si el contenedor de Python se apaga inesperadamente, el front no colapsa, mostrando una tarjeta de error: *"El servicio de búsqueda no está disponible temporalmente"*.
* **Tratamiento No Crítico:** Cualquier error de la API o de red es envuelto en un modelo inofensivo y renderizado por Thymeleaf en tarjetas visuales de error (color rojo), permitiendo al usuario seguir navegando o volver al inicio sin ver la clásica *White Label Error Page*.

---

## 🛠️ Tecnologías Utilizadas

**Frontend:**
* Java 21 / Spring Boot 3
* Spring Security / Spring Data JPA
* Thymeleaf / HTML5 / CSS3 (Grid & Flexbox)
* Chart.js (Gráficos comparativos por Canvas)

**Backend & Infraestructura:**
* Python 3.12 / Flask
* PostgreSQL 15
* Docker & Docker Compose
* PyTest / JUnit 5 / Postman & Newman