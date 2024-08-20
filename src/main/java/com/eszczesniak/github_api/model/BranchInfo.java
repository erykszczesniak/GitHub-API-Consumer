package com.eszczesniak.github_api.model;

import lombok.Data;

@Data
public class BranchInfo {

    private String name;
    private String lastCommitSha;
}