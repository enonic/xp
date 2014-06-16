package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.ContentCompareResult;

public class ContentComparisonJson
{
    private final String compareState;

    private final String id;

    public ContentComparisonJson( final ContentCompareResult contentCompareResult )
    {
        this.compareState = contentCompareResult.getCompareState().getState().name();
        this.id = contentCompareResult.getContentId().toString();
    }

    @SuppressWarnings( "UnusedDeclaration" )
    public String getCompareState()
    {
        return compareState;
    }

    @SuppressWarnings( "UnusedDeclaration" )
    public String getId()
    {
        return id;
    }
}
