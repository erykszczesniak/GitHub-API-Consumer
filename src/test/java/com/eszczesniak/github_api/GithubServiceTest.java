package com.eszczesniak.github_api;

import com.eszczesniak.github_api.exception.UserNotFoundException;
import com.eszczesniak.github_api.model.BranchInfo;
import com.eszczesniak.github_api.model.RepositoryInfo;
import com.eszczesniak.github_api.service.GithubService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;

public class GithubServiceTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final GithubService githubService = new GithubService(restTemplate);

    @Test
    public void testGetRepositories_Success() {
        // Mock repository response
        RepositoryInfo repo = new RepositoryInfo();
        repo.setName("test-repo");
        repo.setFork(false);
        repo.setBranchesUrl("https://api.github.com/repos/test-user/test-repo/branches");

        BranchInfo branch = new BranchInfo();
        branch.setName("main");
        branch.setLastCommitSha("abc123");

        // Mock branch response
        Mockito.when(restTemplate.getForEntity(eq("https://api.github.com/repos/test-user/test-repo/branches"), eq(BranchInfo[].class)))
                .thenReturn(ResponseEntity.ok(new BranchInfo[]{branch}));

        // Mock commit response
        Map<String, Object> commit = new HashMap<>();
        commit.put("sha", "abc123");

        Map<String, Object> commitResponse = new HashMap<>();
        commitResponse.put("commit", commit);

        Mockito.when(restTemplate.getForEntity(eq("https://api.github.com/repos/test-user/test-repo/branches/main"), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(commitResponse));

        Mockito.when(restTemplate.getForEntity(anyString(), eq(RepositoryInfo[].class)))
                .thenReturn(ResponseEntity.ok(new RepositoryInfo[]{repo}));

        List<RepositoryInfo> repos = githubService.getRepositories("test-user");

        assertNotNull(repos);
        assertFalse(repos.isEmpty());
        assertEquals("test-repo", repos.getFirst().getName());
        assertEquals("main", repos.getFirst().getBranches().getFirst().getName());
        assertEquals("abc123", repos.getFirst().getBranches().getFirst().getLastCommitSha());
    }


    @Test
    public void testGetRepositories_UserNotFound() {
        Mockito.when(restTemplate.getForEntity(anyString(), eq(RepositoryInfo[].class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertThrows(UserNotFoundException.class, () -> githubService.getRepositories("nonexistent-user"));
    }
}