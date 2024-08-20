package com.eszczesniak.github_api.service;

import com.eszczesniak.github_api.exception.UserNotFoundException;
import com.eszczesniak.github_api.model.BranchInfo;
import com.eszczesniak.github_api.model.RepositoryInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GithubService {

    private final RestTemplate restTemplate;

    public GithubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepositoryInfo> getRepositories(String username) {
        String url = "https://api.github.com/users/" + username + "/repos";
        try {
            ResponseEntity<RepositoryInfo[]> response = restTemplate.getForEntity(url, RepositoryInfo[].class);
            return Arrays.stream(Objects.requireNonNull(response.getBody()))
                    .filter(repo -> !repo.isFork())
                    .peek(this::populateBranches)
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User " + username + " not found");
        }
    }

    private void populateBranches(RepositoryInfo repositoryInfo) {
        String branchesUrl = repositoryInfo.getBranchesUrl();

        if (branchesUrl != null) {
            branchesUrl = branchesUrl.replace("{/branch}", "");
            ResponseEntity<BranchInfo[]> response = restTemplate.getForEntity(branchesUrl, BranchInfo[].class);
            List<BranchInfo> branches = Arrays.asList(Objects.requireNonNull(response.getBody()));

            for (BranchInfo branch : branches) {
                String commitUrl = branchesUrl + "/" + branch.getName();
                ResponseEntity<Map> commitResponse = restTemplate.getForEntity(commitUrl, Map.class);
                Map commitData = commitResponse.getBody();
                assert commitData != null;
                branch.setLastCommitSha((String) ((Map<?, ?>) commitData.get("commit")).get("sha"));
            }

            repositoryInfo.setBranches(branches);
        } else {
            repositoryInfo.setBranches(List.of());
        }
    }
}