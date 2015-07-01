package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

final class ExportJsonRequest
    implements JsonRequest
{
    @JsonProperty("sourceRepoPath")
    @SuppressWarnings("unused")
    private String sourceRepoPath;

    @JsonProperty("targetDirectory")
    @SuppressWarnings("unused")
    private String targetDirectory;

    @JsonProperty("importWithIds")
    @SuppressWarnings("unused")
    private boolean importWithIds;

    public ExportJsonRequest sourceRepoPath( final String sourceRepoPath )
    {
        this.sourceRepoPath = sourceRepoPath;
        return this;
    }

    public ExportJsonRequest targetDirectory( final String targetDirectory )
    {
        this.targetDirectory = targetDirectory;
        return this;
    }

    public ExportJsonRequest importWithIds( final boolean importWithIds )
    {
        this.importWithIds = importWithIds;
        return this;
    }
}
