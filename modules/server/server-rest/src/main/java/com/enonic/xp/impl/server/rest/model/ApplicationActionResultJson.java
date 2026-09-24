package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ApplicationActionResultJson
{
    private final List<ActionResult> results;

    public ApplicationActionResultJson( final List<ActionResult> results )
    {
        this.results = results;
    }

    public List<ActionResult> getResults()
    {
        return results;
    }

    public record ActionResult(String id, boolean success, @JsonInclude(JsonInclude.Include.NON_NULL) String message)
    {
        public ActionResult( final String id, final boolean success )
        {
            this( id, success, null );
        }
    }
}
