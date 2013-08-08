package com.enonic.wem.admin.rest.resource.schema.content.model;

import com.enonic.wem.admin.rest.resource.schema.content.model.form.FormJson;
import com.enonic.wem.api.schema.content.ContentType;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeJson
    extends ContentTypeSummaryJson
{
    private final FormJson form;

    public ContentTypeJson( final ContentType contentType )
    {
        super( contentType );
        this.form = new FormJson( contentType.form() );
    }

    public FormJson getForm()
    {
        return this.form;
    }
}
