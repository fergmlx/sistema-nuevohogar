# 🐾 Sistema de Gestión para Refugio de Animales - NuevoHogar

<div align="center">

[![Java - 17+](https://img.shields.io/badge/Java-17+-FE7D37)](https://www.oracle.com/in/java/technologies/downloads/)
[![MySQL - 5.7+](https://img.shields.io/badge/MySQL-5.7%2B-blue?logo=mysql)](https://mysql.com)
[![Hibernate - 6.6.13](https://img.shields.io/badge/Hibernate-6.6.13-BCAE79?logo=hibernate)](https://hibernate.org/)
[![Swing - GUI](https://img.shields.io/badge/Swing-GUI-97CA00)]()

</div>

---

<img width="1442" height="931" alt="{3043326E-F1F6-44CD-BB64-2C423FDEFD89}" src="https://github.com/user-attachments/assets/a78bca72-6b36-4bdb-bf58-5f40292d68f2" />

**Número de equipo:** 3

**Integrantes:**  

<div align="center">
  <table>
    <tr>
      <td align="center">
        <a href="https://github.com/fergmlx">
          <img src="https://github.com/fergmlx.png" width="100px;" alt=""/>
          <br />
          <sub><b>González Miguel Luis Fernando</b></sub>
        </a>
        <br />
        <sub>Miembro del equipo</sub>
      </td>
      <td align="center">
        <a href="https://github.com/JonathanRene">
          <img src="https://github.com/JonathanRene.png" width="100px;" alt=""/>
          <br />
          <sub><b>Cruz Gutiérrez Jonathan Rene</b></sub>
        </a>
        <br />
        <sub>Miembro del equipo</sub>
      </td>
    </tr>
  </table>
</div>

---

### ¿Qué hace el sistema?

"NuevoHogar" es un sistema de gestión para refugios de animales que permite el registro, seguimiento y adopción de mascotas, así como la administración de voluntarios, donaciones, eventos y tareas. El sistema facilita el trabajo colaborativo entre administradores, coordinadores, veterinarios y voluntarios.

---

### Tipo de sistema

Desktop App en Java, desarrollada con Swing (NetBeans GUI Designer).

---

### Librería externa implementada

- Usamos la librería [`CorreoElectronico`](https://github.com/olmomomo/Libreria_correoElectronico) del Equipo 2 para el envío de correos.

---

### Componente visual integrado

- Integramos el  [`CaptchaPanel`](https://github.com/FanyBr07/ComponenteVisual) del Equipo 2 en el login.

---

## 3. Funcionalidades Clave con Imágenes

### - Integración de CAPTCHA
- **¿Dónde se usa?**: En el formulario de login para evitar accesos automatizados.
- **Fuente**: CAPTCHA del Equipo 2, ([Link al repositorio](https://github.com/FanyBr07/ComponenteVisual)).

<img width="1442" height="931" alt="{C1DE0988-4AA2-4FAE-8656-6EE3EAB2C2F1}" src="https://github.com/user-attachments/assets/75bb44a9-685b-4120-81b3-880285ff1d28" />

### - CRUD de usuarios
- Permite crear, editar, eliminar y buscar usuarios del sistema, asignando rol (Administrador, Coordinador, Veterinario, Voluntario).

<img width="1442" height="931" alt="{D6C0E8C2-9F86-4E78-AA93-B7D9C367CCB1}" src="https://github.com/user-attachments/assets/11d9c882-0e70-4ef5-abca-5926dd6e5b24" />

<div align="center">
<img width="491" height="376" alt="{3809BC21-9DAC-48C3-B34C-AD2B781867DA}" src="https://github.com/user-attachments/assets/38e0cedc-e0b3-44f8-99f7-cdad38adff7f" />
</div>

### - CRUD de animales
- Registro de nuevo animal (con foto), edición de datos, eliminación (si no está adoptado), búsqueda avanzada y filtro por estado.
<img width="1442" height="931" alt="{A5F7CFFF-369F-4DCF-9D6E-9E2CD5401E79}" src="https://github.com/user-attachments/assets/c065930a-ec64-4fa2-837a-4ecb59a52aad" />

<img width="1442" height="931" alt="{4E0AF281-EB4D-4A5E-8A4A-4338FED2823D}" src="https://github.com/user-attachments/assets/d3fe3470-6eb6-4f1b-91e7-ea840aafa063" />

### - CRUD de historial médico
- Permite a veterinarios registrar, editar y consultar atenciones médicas de cada animal (consultas, vacunas, tratamientos, diagnósticos, síntomas).
- El historial se vincula al animal y puede ser consultado por administradores y coordinadores (solo lectura).
- Incluye programación de próximas revisiones.
<img width="1442" height="931" alt="{50BB1B7B-BCC3-4CCB-BF2A-566084FC9F43}" src="https://github.com/user-attachments/assets/72e1c740-9039-417a-bf9e-a6f77ffd9b84" />


### - CRUD de adopciones
- Registro de nuevas solicitudes de adopción, edición, aprobación/rechazo/finalización según flujo y rol.  
- Al aprobar/rechazar: el usuario logueado se asigna como aprobador y se registra la fecha de resolución.
<img width="1442" height="931" alt="{F6339A44-E042-411D-AE6D-C503AABB6FEE}" src="https://github.com/user-attachments/assets/f76cdbd4-0014-4921-850f-25a51e154f6c" />

### - CRUD de tareas y voluntariado
- Asignación y seguimiento de tareas a voluntarios, con registro de cumplimiento y reporte de actividades.
<img width="1442" height="931" alt="{6083607D-88F4-49F3-BF55-EB21F8841DC8}" src="https://github.com/user-attachments/assets/08b792c1-2407-4962-8ba3-6af989cd5171" />
<img width="1442" height="931" alt="{4BC01EF9-F452-4B1A-A134-C465BD955D94}" src="https://github.com/user-attachments/assets/8de2291d-bc0e-4860-bc0d-c7ad709d3ee1" />

### - CRUD de donaciones e inventario
- Registro de donaciones (monetarias y en especie), vínculo a inventario, control de stock y alertas de bajos insumos.
<img width="1439" height="929" alt="{8BE77D98-78B3-4A3C-B864-08E872194845}" src="https://github.com/user-attachments/assets/df751931-ee8d-4d62-9991-06cebc0093ea" />
<img width="1438" height="926" alt="{3500A25F-7BDC-4FD9-82D9-E85212BFB8D0}" src="https://github.com/user-attachments/assets/2b762303-a2ce-40de-b35f-a801828a85bc" />

### 📧 Envío de Correo Electrónico con PDF Adjunto  
**Generación de PDF**: Utilizamos OpenHTMLToPDF para convertir plantillas HTML personalizadas por rol en documentos PDF profesionales.
  
**Plantillas por Rol**: Sistema de plantillas diferenciadas para cada tipo de usuario (Veterinario, Administrador, Coordinador, Voluntario) con información específica de funciones.
  
**Envío Automático**: Modificamos el código de la librería del Equipo 2 para poder pasar un archivo HMTL como cuerpo y, también, para permitir la integración con Zoho Mail para envío automático de correos de bienvenida con documentos adjuntos al registrar nuevos usuarios.  

### - Proceso principal: Adopción de mascotas
- Flujo desde la solicitud hasta la aprobación/finalización, con registro de seguimiento y entrega.

---

## 4. Dependencias y Configuración

### - Librerías externas usadas

- FlatLaf (estilos visuales)
- OpenHTMLToPDF (PDF)
- CorreoElectronico, JavaMail (correo)
- Raven Modal, Raven Toast (UI)
- Hibernate, HikariCP (manejo de base de datos)

### - Pasos para instalar y ejecutar

1. Clonar el repositorio.
2. Abrir el proyecto en NetBeans.
3. Agregar todos los JAR localizados en [librerias](https://github.com/fergmlx/sistema-nuevohogar/tree/main/librerias) al classpath.
4. Copia el archivo `.env.example` y renómbralo a `.env`.
5. Completa tus credenciales y parámetros en `.env`.
6. Ejecutar el archivo `Main.java` para iniciar la aplicación.

> [!NOTE]
> Parámetros del `.env`

```
# URL de la base de datos MySQL (puerto y nombre de la base de datos)
DB_URL=jdbc:mysql://localhost:3306/nuevohogar

# Usuario de la base de datos
DB_USER=root

# Contraseña del usuario de la base de datos
DB_PASS=tu_contraseña

# Dirección de correo electrónico usada como remitente para notificaciones
SMTP_USER=no-reply@nuevohogar.com

# Contraseña de aplicacióno del servicio de correo usado
SMTP_APP_PASSWORD=tu_app_password
```

### - Requisitos mínimos

- Java 17+ 
- MySQL 5.7+ (recomendado 8.0+)
- NetBeans 15+

---
