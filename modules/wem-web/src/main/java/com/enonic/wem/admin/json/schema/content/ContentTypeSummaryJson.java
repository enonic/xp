package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentType;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeSummaryJson
    extends ItemJson
{
    private final ContentType contentType;

    private final String iconUrl;

    private final boolean editable;

    private final boolean deletable;

    public ContentTypeSummaryJson( final ContentType contentType )
    {
        this.contentType = contentType;
        this.iconUrl = SchemaImageUriResolver.resolve( contentType.getSchemaKey() );
        this.editable = !this.contentType.isFinal();
        this.deletable = !ModuleName.SYSTEM.equals( this.contentType.getModuleName() );
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

    public boolean isAllowChildContent()
    {
        return this.contentType.allowChildContent();
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }
}
