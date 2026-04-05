package com.enonic.xp.impl.server.rest.model;

import java.util.List;

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

    public record ActionResult(String id, boolean success)
    {
    }
}
