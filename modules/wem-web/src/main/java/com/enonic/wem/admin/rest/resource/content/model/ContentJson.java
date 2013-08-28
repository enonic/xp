package com.enonic.wem.admin.rest.resource.content.model;

import com.enonic.wem.api.content.Content;

public class ContentJson
    extends ContentSummaryJson
{
    private final DataSetJson data;

    public ContentJson( Content content )
    {
        super( content );
        this.data = new DataSetJson( content.getContentData() );
    }

    public DataSetJson getData()
    {
        return data;
    }
}
