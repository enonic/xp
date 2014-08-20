package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.schema.content.ContentType;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeJson
    extends ContentTypeSummaryJson
{
    private final FormJson form;

    public ContentTypeJson( final ContentType contentType, final SchemaIconUrlResolver iconUrlResolver )
    {
        super( contentType, iconUrlResolver );
        this.form = new FormJson( contentType.form() );
    }

    public FormJson getForm()
    {
        return this.form;
    }
}
