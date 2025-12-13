# Technical Documentation

## External Rating API

The application integrates with the OMDb API (Open Movie Database) to enrich movie records with official IMDb ratings.

**Reasons for choosing OMDb**

- Provides IMDb ratings and metadata
- Simple REST interface
- Free tier available (up to 1000 requests per day)
- No complex authentication (API key-based access)

**Integration Details**

- The OMDb API is queried using the movie title
- If a matching result is found, the IMDb rating is extracted
- The rating is stored in the local database
- API access is configured via an external API key stored in application-secrets.properties

This approach allows the application to remain independent of external data availability while still enhancing stored movie information.

## Authentication & Authorization

**Authentication**

The application uses JWT-based authentication implemented with Spring Security.

Authentication flow:
- A user logs in using a username and password
- Credentials are validated using Spring Security's AuthenticationManager
- On successful authentication, a JWT is generated
- The JWT is returned via cookie and used for subsequent requests

Each incoming request:
- Includes the JWT in the cookie
- Is authenticated using a custom JWT Filter
  Does not rely on server-side sessions (stateless authentication)

**Authorization**

Authorization is implemented using method-level security via @PreAuthorize.
- User roles are hashed in the JWT
- Roles are resolved into Spring Security authorities
- Access rules are enforced at the controller level

Defined roles:
- *USER* - read-only access to movie data, create and update access on personal user information
- *ADMIN* - full CRUD access to movies and users

This approach ensures that authorization rules apply consistently, regardless of how a controller method is invoked.

## Asynchronous Rating Enrichment

To avoid blocking the movie creation request, external rating enrichment is implemented asynchronously.

**Workflow**

- A movie is created and saved in the database
- The API responds immediately to the client
- A background task queries the OMDb API
- If a rating is found, the movie record is updated asynchronously
- Each movie record has a state (Pending, Successful, or Failed), indicating the enrichment status.

This design ensures that the application remains responsive even when the external API is slow or temporarily unavailable.

## Architectural Decisions & Trade-offs

**Stateless Authentication**

- JWT-based authentication was chosen over session-based authentication
- Eliminates server-side session storage
- Scales well for REST APIs
- Requires token management

**Method-level Authorization**

- Authorization rules are enforced at the controller layer
- Improves reusability and security consistency

**Asynchronous External Integration**

- External API calls are decoupled from core business operations
- Prevents external dependencies from degrading system performance
- Introduces eventual consistency for movie ratings

**Trade-offs**

- Asynchronous enrichment means ratings may not be immediately available
- JWT tokens must be securely stored and rotated if compromised
- External API rate limits must be respected

## Conclusion

The Movie Library application demonstrates a clean and scalable architecture using Spring Boot and Spring Security. By combining JWT-based authentication, method-level authorization, and asynchronous external integration, the system achieves a balance between security, performance, and maintainability.