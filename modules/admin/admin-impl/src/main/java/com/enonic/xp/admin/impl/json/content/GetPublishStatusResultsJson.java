package com.enonic.xp.admin.impl.json.content;

import java.util.List;

public class GetPublishStatusResultsJson
{
    private final List<GetPublishStatusResultJson> getPublishStatusResults;

    public GetPublishStatusResultsJson( final List<GetPublishStatusResultJson> getPublishStatusResults )
    {
        this.getPublishStatusResults = getPublishStatusResults;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<GetPublishStatusResultJson> getGetPublishStatusResults()
    {
        return getPublishStatusResults;
    }
}
