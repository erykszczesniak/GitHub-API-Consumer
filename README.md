# GitHub API Consumer

## Overview

This project is a Spring Boot application that exposes a REST API to fetch GitHub repositories for a given user. The API returns repositories that are not forks, including details about each branch and its latest commit SHA. If the user does not exist, the API returns a 404 response with a structured error message.

## Technologies

- Java 21 
- Spring Boot 3
- Maven 
- RestTemplate 
- GitHub API
- Lombok
- JUnit 5
- Mockito

## Getting Started

### Prerequisites

- Java 21
- Maven 
- A GitHub account

### Setting Up GitHub Personal Access Token

To avoid GitHub's API rate limiting, it is recommended to use a GitHub Personal Access Token (PAT). This token will allow you to make authenticated requests with a higher rate limit.

#### Steps to Set Up the Token

1. **Create a GitHub Personal Access Token:**
   - Go to your GitHub account settings.
   - Navigate to "Developer settings" -> "Personal access tokens" -> "Tokens (classic)".
   - Generate a new token with the necessary permissions, such as `repo` and `read:user`.
   - Copy the token to a secure place.

2. **Configure the Token in AppConfig:**
   - Open the `AppConfig.java` file located at `src/main/java/com/example/githubapi/config/AppConfig.java`.
   - Replace `YOUR_PERSONAL_ACCESS_TOKEN` with your actual GitHub Personal Access Token.

```java
package com.example.githubapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class AppConfig {

    private static final String GITHUB_TOKEN = "YOUR_PERSONAL_ACCESS_TOKEN";  // Replace with your token

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Add an interceptor to include the Authorization header with the token
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.add("Authorization", "Bearer " + GITHUB_TOKEN);
            return execution.execute(request, body);
        };
        
        restTemplate.setInterceptors(Collections.singletonList(authInterceptor));
        return restTemplate;
    }
}
```

## Running the Application

1. Clone the repository.
2. Navigate to the project directory.
3. Run the application using your IDE or via the command line:

```bash
./mvnw spring-boot:run
```

The application will start on [http://localhost:8081](http://localhost:8081).

## API Endpoints

### Get Repositories for a User: `/api/github/repositories/{username}`
- **Method:** GET
- **Headers:** `Accept: application/json`
- **Response:** List of repositories that are not forks, including branch details.

### Example Request
```bash
curl -H "Accept: application/json" http://localhost:8081/api/github/repositories/user
```

### Example Response
```json
[
  {
   "name": "configs",
    "ownerLogin": "user",
    "branches": [
      {
        "name": "master",
        "lastCommitSha": "3cb43196cdf6129b40cd728a5d1a008d77b31f62"
      }
    ]
  }
]
```


https://github.com/user-attachments/assets/1ceded8d-2ad7-4562-9e4c-e565b30865a3


### Error Handling
If the user does not exist, the API will return a 404 response:

```json
{
  "status": 404,
  "message": "User {username} not found"
}
```



https://github.com/user-attachments/assets/fb52ed23-888b-4f9e-9c6f-37af95c876e8



## Testing
Unit tests and integration tests are provided to validate the application. You can run the tests using:

```bash
./mvnw test
```

