package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.ContentCompareResult;

public class ContentCompareResultJson
{
    private final String compareStatus;

    private final String id;

    public ContentCompareResultJson( final ContentCompareResult contentCompareResult )
    {
        this.compareStatus = contentCompareResult.getCompareStatus().getState().name();
        this.id = contentCompareResult.getContentId().toString();
    }

    @SuppressWarnings( "UnusedDeclaration" )
    public String getCompareStatus()
    {
        return compareStatus;
    }

    @SuppressWarnings( "UnusedDeclaration" )
    public String getId()
    {
        return id;
    }
}
