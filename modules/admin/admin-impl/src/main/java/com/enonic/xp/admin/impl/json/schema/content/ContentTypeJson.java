package com.enonic.xp.admin.impl.json.schema.content;

import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleResolver;
import com.enonic.xp.schema.content.ContentType;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeJson
    extends ContentTypeSummaryJson
{
    private final FormJson form;

    public ContentTypeJson( final ContentType contentType, final ContentTypeIconUrlResolver iconUrlResolver,
                            final LocaleResolver localeResolver )
    {
        super( contentType, iconUrlResolver, localeResolver );
        this.form = new FormJson( contentType.getForm(), localeResolver );
    }

    public FormJson getForm()
    {
        return this.form;
    }
}
