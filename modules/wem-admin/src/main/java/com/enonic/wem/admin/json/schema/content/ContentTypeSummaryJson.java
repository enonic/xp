package com.enonic.wem.admin.json.schema.content;

import com.enonic.wem.admin.json.ChangeTraceableJson;
import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.schema.SchemaJson;
import com.enonic.wem.api.schema.content.ContentType;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeSummaryJson
    extends SchemaJson
    implements ItemJson, ChangeTraceableJson
{
    private final ContentType contentType;

    private final boolean editable;

    private final boolean deletable;

    public ContentTypeSummaryJson( final ContentType contentType )
    {
        super( contentType );
        this.contentType = contentType;
        this.editable = !this.contentType.isBuiltIn();
        this.deletable = !this.contentType.isBuiltIn();
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

    public String getCreator()
    {
        return contentType.getCreator() != null ? contentType.getCreator().toString() : null;
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
