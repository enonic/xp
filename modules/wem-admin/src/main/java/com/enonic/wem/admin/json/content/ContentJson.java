package com.enonic.wem.admin.json.content;

import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.DataSetJson;
import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.content.Content;

@SuppressWarnings("UnusedDeclaration")
public class ContentJson
    extends ContentSummaryJson
{
    private final DataSetJson data;

    private final FormJson form;

    public ContentJson( final Content content )
    {
        super( content );
        this.data = new DataSetJson( content.getContentData() );
        this.form = content.getForm() != null ? new FormJson( content.getForm() ) : null;
    }

    public List<DataJson> getData()
    {
        return data.getValue();
    }

    public FormJson getForm()
    {
        return form;
    }
}
