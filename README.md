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

```
iam-service/
├── src/
│   ├── main/
│   │   ├── java/upc/edu/muusmart/iamservice/
│   │   │   ├── controller/        # Controladores REST (AuthController)
│   │   │   ├── service/           # Lógica de negocio (UserService, CustomUserDetailsService)
│   │   │   ├── repository/        # Interfaces JPA (UserRepository)
│   │   │   ├── model/             # Entidades JPA (AppUser, Role)
│   │   │   ├── security/          # Configuración JWT y Spring Security
│   │   │   └── payload/           # Objetos de entrada/salida (DTOs)
│   │   └── resources/
│   │       └── application.properties
└── pom.xml
```

---

##  3. Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| `POST` | `/auth/register` | Registra un nuevo usuario |
| `POST` | `/auth/login` | Autentica un usuario y devuelve un token JWT |

###  Ejemplo de cuerpo para registro

```json
POST /auth/register

{
  "username": "jhordan",
  "email": "jhordan@example.com",
  "password": "123456"
}
```

###  Ejemplo de cuerpo para login

```json
POST /auth/login

{
  "username": "jhordan",
  "password": "123456"
}
```

**Respuesta exitosa:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
}
```

---

##  4. Configuración de la base de datos

Archivo: `application.properties`

```properties
# Configuración de MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/iam_db
spring.datasource.username=root
spring.datasource.password=tu_contraseña

# Configuración de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuración JWT
jwt.secret=ClaveSuperSecretaParaJWT
jwt.expiration=86400000  # 24 horas (en milisegundos)
```

---

##  5. Creación de un usuario administrador

Por defecto, el endpoint `/auth/register` crea usuarios con el rol `ROLE_USER`.

Para crear un usuario administrador (`ROLE_ADMIN`) tienes dos opciones:

### **Opción A — Insertar manualmente en MySQL**

Ejecuta el siguiente SQL (recuerda cifrar la contraseña con bcrypt):

```sql
INSERT INTO app_user (username, email, password, roles)
VALUES ('admin', 'admin@muusmart.com', '$2a$10$JxVJ8Fh6nZK9q9IajmC2geE1BY7j7E1W84xD..', 'ROLE_ADMIN');
```

> **Nota:** Usa una contraseña cifrada generada por Spring Security. Puedes obtenerla ejecutando:
> 
> ```java
> new BCryptPasswordEncoder().encode("admin123");
> ```

### **Opción B — Método temporal en UserService**

Agrega el siguiente fragmento solo para desarrollo:

```java
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
```

---

## 6. Seguridad JWT

El sistema de seguridad se compone de tres elementos clave:

1. **JwtUtil**: Genera y valida los tokens con un secreto configurado en `application.properties`

2. **JwtAuthenticationFilter**: Intercepta las solicitudes, extrae el token del encabezado `Authorization: Bearer <token>` y autentica al usuario

3. **SecurityConfig**: Define qué endpoints son públicos y cuáles requieren autenticación

### Flujo de autenticación

```
1. Usuario hace POST /auth/login
2. Sistema valida credenciales
3. Sistema genera token JWT
4. Cliente guarda el token
5. Cliente envía token en header: Authorization: Bearer <token>
6. Sistema valida token y permite acceso
```

---

## 7. Integración con Eureka y Gateway

El servicio está diseñado para integrarse en una arquitectura de microservicios con:

- **Eureka Server**: Registro y descubrimiento del servicio (`@EnableEurekaClient`)
- **API Gateway**: Manejo centralizado de las peticiones (`/auth/**`)

### Ejemplo de configuración en el Gateway

Archivo: `application.yml` del Gateway

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: iam-service
          uri: lb://IAM-SERVICE
          predicates:
            - Path=/auth/**
```

---

## 8. Buenas prácticas

- ✔️ **Nunca almacenes contraseñas en texto plano**
- ✔️ **Mantén tu `jwt.secret` fuera del repositorio** (usa variables de entorno)
- ✔️ **Implementa control de roles** en endpoints sensibles
- ✔️ **Usa `@PreAuthorize("hasRole('ADMIN')")`** en controladores restringidos
- ✔️ **Habilita HTTPS en producción**

### Ejemplo de endpoint protegido

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
}
```

---

## 9. Resultado esperado

 Usuarios pueden registrarse y autenticarse correctamente

 Las peticiones autenticadas llevan el token JWT en la cabecera:

```
Authorization: Bearer <token>
```

El servicio se comunica fácilmente con otros módulos del ecosistema (Salud, Producción, Campañas, etc.) a través del Gateway

---

##  10. Próximos pasos

- [ ] Añadir **refresh tokens** y expiración avanzada
- [ ] Implementar **cambio de contraseña** y **revocación de tokens**
- [ ] Registrar el servicio en **Eureka** y exponerlo al **Gateway**
- [ ] Configurar **métricas y logs** para auditoría de accesos
- [ ] Implementar **autenticación de dos factores (2FA)**
- [ ] Agregar **endpoints de recuperación de contraseña**

---

##  Resumen

Este microservicio IAM es la **base de seguridad y autenticación** de Bovinova/MuuSmart.

Está construido con buenas prácticas de **Spring Boot + JWT + JPA**, modularizado para integrarse fácilmente en una arquitectura de microservicios **escalable y mantenible**.

### Arquitectura de seguridad

```
Cliente → API Gateway → IAM Service → MySQL
                ↓
         Valida JWT
                ↓
         Otros Microservicios
```
