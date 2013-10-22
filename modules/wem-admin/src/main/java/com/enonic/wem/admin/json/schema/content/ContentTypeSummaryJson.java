package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.admin.json.ChangeTraceableJson;
import com.enonic.wem.admin.json.DateTimeFormatter;
import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.ContentType;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeSummaryJson
    implements ItemJson, ChangeTraceableJson
{
    private final ContentType contentType;

    private final String iconUrl;

    private final boolean editable;

    private final boolean deletable;

    public ContentTypeSummaryJson( final ContentType contentType )
    {
        this.contentType = contentType;
        this.iconUrl = SchemaImageUriResolver.resolve( contentType.getSchemaKey() );
        this.editable = !this.contentType.isBuiltIn();
        this.deletable = !this.contentType.isBuiltIn();
    }

    public String getName()
    {
        return this.contentType.getName();
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

    public String getCreatedTime()
    {
        return DateTimeFormatter.format( contentType.getCreatedTime() );
    }

    public String getCreator()
    {
        return contentType.getCreator() != null ? contentType.getCreator().toString() : null;
    }

    public String getModifiedTime()
    {
        return contentType.getModifiedTime() != null ? DateTimeFormatter.format( contentType.getModifiedTime() ) : null;
    }

    public String getModifier()
    {
        return contentType.getModifier() != null ? contentType.getModifier().toString() : null;
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
