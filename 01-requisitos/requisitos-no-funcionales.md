# Requisitos no funcionales - Pedidos de la cafetería universitaria

**Release:** 1.0  
**Versión del documento:** 1.0  

## 1. Requisitos no funcionales y datos iniciales

| ID | Requisito | Cómo se verifica | Origen |
|----|-----------|------------------|--------|
| RNF-01 | **Persistencia.** La información de clientes, productos y pedidos se conserva entre ejecuciones. | Registrar datos, reiniciar la aplicación y comprobar que siguen disponibles sin cambios. | Cliente, apartado 8 |
| RNF-02 | Las contraseñas se almacenan cifradas (hash) y no aparecen en texto plano en la base de datos ni en los registros de la aplicación. | Revisar la tabla de usuarios y los registros tras un registro y un inicio de sesión. | Derivado |
| RNF-03 | El control de acceso por rol se aplica en el servidor, no solo ocultando opciones en pantalla. | Solicitar directamente la dirección de una vista o acción restringida con un rol sin permiso y comprobar que se deniega. | Derivado |
| RNF-04 | Las validaciones (formato, rangos y reglas de negocio) se aplican en el servidor aunque la interfaz también las valide. | Enviar datos inválidos sin pasar por la interfaz y comprobar que se rechazan. | Derivado |
| RNF-05 | La base de datos garantiza la unicidad del código de retiro, del correo, de la cédula o RUC y del nombre de producto. | Intentar registrar duplicados, incluso en dos solicitudes simultáneas, y comprobar que se rechazan. | Cliente (RN-NEG-05) y derivado |
| RNF-06 | Un pedido y sus detalles se registran de forma atómica: si algo falla, no queda un pedido parcial. | Simular un fallo durante la confirmación y comprobar que no se crea el pedido ni sus detalles. | Derivado |
| RNF-07 | **Datos iniciales para la demostración.** Una instalación nueva incluye la cuenta del administrador, una cuenta de personal, una cuenta de cliente de prueba y varios productos de ejemplo, al menos uno no disponible. | Ejecutar los scripts de datos iniciales sobre una base vacía y comprobar que los datos existen. | Cliente, apartado 8 («demostrar los procesos y las reglas») |

Las credenciales de las cuentas iniciales se documentan en el manual de instalación.