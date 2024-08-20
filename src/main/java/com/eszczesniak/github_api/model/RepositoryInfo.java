package com.eszczesniak.github_api.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RepositoryInfo {

    private String name;

    @JsonProperty("owner")
    private void unpackOwner(Map<String, Object> owner) {
        this.ownerLogin = (String) owner.get("login");
    }

    private String ownerLogin;

    @JsonProperty("fork")
    private boolean fork;

    @JsonProperty("branches_url")
    private String branchesUrl;

    private List<BranchInfo> branches;
}