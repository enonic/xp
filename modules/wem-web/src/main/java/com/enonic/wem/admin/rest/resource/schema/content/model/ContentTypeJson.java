package com.enonic.wem.admin.rest.resource.schema.content.model;

import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.admin.rest.resource.schema.content.model.form.FormItemListJson;
import com.enonic.wem.api.schema.content.ContentType;

public class ContentTypeJson
{
    private final ContentType contentType;

    private FormItemListJson list;

    private final String iconUrl;

    public ContentTypeJson( final ContentType contentType )
    {
        this.contentType = contentType;
        if ( this.contentType.form() != null )
        {
            this.list = new FormItemListJson( this.contentType.form() );
        }
        this.iconUrl = SchemaImageUriResolver.resolve( contentType.getSchemaKey() );
    }

    public String getName()
    {
        return this.contentType.getName();
    }

    public String getModule()
    {
        return this.contentType.getModuleName().toString();
    }

    public String getQualifiedName()
    {
        return this.contentType.getQualifiedName().toString();
    }

    public String getDisplayName()
    {
        return this.contentType.getDisplayName();
    }

    public String getContentDisplayNameScript()
    {
        return this.contentType.getContentDisplayNameScript();
    }

    public String getSuperType()
    {
        return this.contentType.getSuperType() != null ? this.contentType.getSuperType().toString() : null;
    }

    public boolean isAbstract()
    {
        return this.contentType.isAbstract();
    }

    public boolean isFinal()
    {
        return this.contentType.isFinal();
    }

    public boolean isAllowChildren()
    {
        return this.contentType.allowChildren();
    }

    public FormItemListJson getForm()
    {
        return this.list;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }
}
