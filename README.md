# Microservicio de Autenticación — IAM Service (Spring Boot 3.5.7)

Este microservicio forma parte de la arquitectura de **Bovinova/MuuSmart**, cumpliendo el rol de **Gestión de Identidad y Acceso (IAM)** dentro del ecosistema de siete microservicios distribuidos.  
Su objetivo es autenticar y autorizar usuarios mediante **JWT (JSON Web Tokens)** y **Spring Security**, brindando endpoints para **registro** y **login**.

---

## 1. Tecnologías y dependencias principales

| Componente | Descripción |
|-------------|--------------|
| **Spring Boot 3.5.7** | Framework base del microservicio |
| **Java 17** | Versión recomendada del JDK |
| **Spring Web** | Crea los endpoints REST |
| **Spring Security** | Módulo de seguridad y autenticación |
| **Spring Data JPA** | Acceso a base de datos con Hibernate |
| **MySQL Driver** | Conexión con base de datos MySQL |
| **Validation** | Validación de datos de entrada |
| **Lombok** | Reduce código repetitivo (Getters, Setters, Builders) |
| **DevTools** | Reinicio automático para desarrollo |

---

##  2. Estructura del proyecto

iam-service/
├── src/
│ ├── main/java/upc/edu/muusmart/iamservice/
│ │ ├── controller/ # Controladores REST (AuthController)
│ │ ├── service/ # Lógica de negocio (UserService, CustomUserDetailsService)
│ │ ├── repository/ # Interfaces JPA (UserRepository)
│ │ ├── model/ # Entidades JPA (AppUser, Role)
│ │ ├── security/ # Configuración JWT y Spring Security
│ │ └── payload/ # Objetos de entrada/salida (DTOs)
│ └── main/resources/
│ └── application.properties
└── pom.xml

yaml
Copiar código

---

## 3. Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| `POST` | `/auth/register` | Registra un nuevo usuario |
| `POST` | `/auth/login` | Autentica un usuario y devuelve un token JWT |

### Ejemplo de cuerpo para registro

```json
POST /auth/register
{
  "username": "jhordan",
  "email": "jhordan@example.com",
  "password": "123456"
}
🧾 Ejemplo de cuerpo para login
json
Copiar código
POST /auth/login
{
  "username": "jhordan",
  "password": "123456"
}
Respuesta exitosa:

json
Copiar código
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
}
4. Configuración de la base de datos (application.properties)
properties
Copiar código
spring.datasource.url=jdbc:mysql://localhost:3306/iam_db
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Clave secreta para firmar tokens JWT
jwt.secret=ClaveSuperSecretaParaJWT
jwt.expiration=86400000  # 24 horas

5. Creación de un usuario administrador
Por defecto, el endpoint /auth/register crea usuarios con el rol ROLE_USER.
Para crear un usuario administrador (ROLE_ADMIN) puedes hacerlo manualmente desde la base de datos o añadiendo un método temporal al UserService.

Opción A — Insertar manualmente en MySQL
Ejecuta el siguiente SQL (recuerda cifrar la contraseña con bcrypt si ya tienes el PasswordEncoder activo):

sql
Copiar código
INSERT INTO app_user (username, email, password, roles)
VALUES ('admin', 'admin@muusmart.com', '$2a$10$JxVJ8Fh6nZK9q9IajmC2geE1BY7j7E1W84xD..', 'ROLE_ADMIN');

Usa una contraseña cifrada generada por Spring Security.
Por ejemplo, puedes obtenerla temporalmente ejecutando un PasswordEncoder en un test o desde el IDE:

java
new BCryptPasswordEncoder().encode("admin123");
Opción B — Método temporal en UserService
Agrega el siguiente fragmento solo para desarrollo (puedes llamarlo desde IamServiceApplication):

java
Copiar código
@PostConstruct
public void createAdminUser() {
    if (userRepository.findByUsername("admin").isEmpty()) {
        AppUser admin = AppUser.builder()
                .username("admin")
                .email("admin@muusmart.com")
                .password(passwordEncoder.encode("admin123"))
                .roles(Set.of(Role.ROLE_ADMIN))
                .build();
        userRepository.save(admin);
        System.out.println("✅ Usuario admin creado con éxito.");
    }
}
6. Seguridad JWT
JwtUtil genera y valida los tokens con un secreto configurado en application.properties.

JwtAuthenticationFilter intercepta las solicitudes, extrae el token del encabezado Authorization: Bearer <token>, y autentica al usuario.

SecurityConfig define qué endpoints son públicos y cuáles requieren autenticación.

7. Integración con Eureka y Gateway
El servicio está diseñado para integrarse en una arquitectura de microservicios con:

Eureka Server: registro y descubrimiento del servicio (@EnableEurekaClient).

API Gateway: manejo centralizado de las peticiones (/auth/**).

Ejemplo de configuración en application.yml del Gateway:

yaml
Copiar código
spring:
  cloud:
    gateway:
      routes:
        - id: iam-service
          uri: lb://IAM-SERVICE
          predicates:
            - Path=/auth/**
8. Buenas prácticas
Nunca almacenes contraseñas en texto plano.

Mantén tu jwt.secret fuera del repositorio (usa variables de entorno).

Implementa control de roles en endpoints sensibles.

Usa @PreAuthorize("hasRole('ADMIN')") en controladores restringidos.

Habilita HTTPS en producción.

9. Resultado esperado
Usuarios pueden registrarse y autenticarse correctamente.

Las peticiones autenticadas llevan el token JWT en la cabecera:

makefile
Copiar código
Authorization: Bearer <token>
El servicio se comunica fácilmente con otros módulos del ecosistema (Salud, Producción, Campañas, etc.) a través del Gateway.

10. Próximos pasos
Añadir refresh tokens y expiración avanzada.

Implementar cambio de contraseña y revocación de tokens.

Registrar el servicio en Eureka y exponerlo al Gateway.

Configurar métricas y logs para auditoría de accesos.

Resumen:
Este microservicio IAM es la base de seguridad y autenticación de Bovinova/MuuSmart.
Está construido con buenas prácticas de Spring Boot + JWT + JPA, modularizado para integrarse fácilmente en una arquitectura de microservicios escalable y mantenible.
