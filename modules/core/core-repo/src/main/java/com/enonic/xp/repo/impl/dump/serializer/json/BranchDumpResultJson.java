package com.enonic.xp.repo.impl.dump.serializer.json;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;

public class BranchDumpResultJson
{
    @JsonProperty
    private Long successful;

    @JsonProperty
    private List<String> errors;

    @SuppressWarnings("unused")
    public BranchDumpResultJson()
    {
    }

    private BranchDumpResultJson( final Long successful, final List<String> errors )
    {
        this.successful = successful;
        this.errors = errors;
    }

    public static BranchDumpResultJson from( final BranchDumpResult branchResult )
    {
        final List<String> errors = branchResult.getErrors().stream().map( DumpError::toString ).collect( Collectors.toList() );
        return new BranchDumpResultJson( branchResult.getSuccessful(), errors );
    }

    public Long getSuccessful()
    {
        return successful;
    }

    public void setSuccessful( final Long successful )
    {
        this.successful = successful;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors( final List<String> errors )
    {
        this.errors = errors;
    }
}
