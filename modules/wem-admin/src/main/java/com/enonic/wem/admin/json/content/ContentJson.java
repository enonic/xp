package com.enonic.wem.admin.json.content;

import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.DataSetJson;
import com.enonic.wem.api.content.Content;

@SuppressWarnings("UnusedDeclaration")
public class ContentJson
    extends ContentSummaryJson
{
    private final DataSetJson data;

    public ContentJson( Content content )
    {
        super( content );
        this.data = new DataSetJson( content.getContentData() );
    }

    public List<DataJson> getData()
    {
        return data.getValue();
    }
}
