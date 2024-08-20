package com.eszczesniak.github_api;

import com.eszczesniak.github_api.controller.GithubController;
import com.eszczesniak.github_api.exception.UserNotFoundException;
import com.eszczesniak.github_api.model.RepositoryInfo;
import com.eszczesniak.github_api.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubController.class)
public class GithubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubService githubService;

    @BeforeEach
    public void setUp() {
        RepositoryInfo repo = new RepositoryInfo();
        repo.setName("test-repo");
        repo.setOwnerLogin("test-user");
        repo.setFork(false);

        Mockito.when(githubService.getRepositories("test-user"))
                .thenReturn(Collections.singletonList(repo));

        Mockito.when(githubService.getRepositories("nonexistent-user"))
                .thenThrow(new UserNotFoundException("User nonexistent-user not found"));
    }

    @Test
    public void testGetRepositories_Success() throws Exception {
        mockMvc.perform(get("/api/github/repositories/test-user")
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("test-repo")))
                .andExpect(jsonPath("$[0].ownerLogin", is("test-user")));
    }

    @Test
    public void testGetRepositories_UserNotFound() throws Exception {
        mockMvc.perform(get("/api/github/repositories/nonexistent-user")
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("User nonexistent-user not found")));
    }
}