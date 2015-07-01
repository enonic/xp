package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

final class ImportJsonRequest
    implements JsonRequest
{
    @JsonProperty("sourceDirectory")
    @SuppressWarnings("unused")
    private String sourceDirectory;

    @JsonProperty("targetRepoPath")
    @SuppressWarnings("unused")
    private String targetRepoPath;

    @JsonProperty("importWithIds")
    @SuppressWarnings("unused")
    private boolean importWithIds;

    public ImportJsonRequest sourceDirectory( final String sourceDirectory )
    {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    public ImportJsonRequest targetRepoPath( final String targetRepoPath )
    {
        this.targetRepoPath = targetRepoPath;
        return this;
    }

    public ImportJsonRequest importWithIds( final boolean importWithIds )
    {
        this.importWithIds = importWithIds;
        return this;
    }
}
