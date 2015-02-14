package com.enonic.xp.admin.impl.json.schema.content;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.ChangeTraceableJson;
import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.core.schema.content.ContentType;
import com.enonic.xp.core.schema.mixin.MixinName;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeSummaryJson
    implements ItemJson, ChangeTraceableJson
{
    private final ContentType contentType;

    private final String iconUrl;

    private final ImmutableList<String> metadataMixinNames;

    public ContentTypeSummaryJson( final ContentType contentType, final ContentTypeIconUrlResolver iconUrlResolver )
    {
        this.contentType = contentType;
        this.iconUrl = iconUrlResolver.resolve( contentType );

        ImmutableList.Builder<String> mixinNamesBuilder = new ImmutableList.Builder<>();
        if ( this.contentType.getMetadata() != null )
        {
            for ( MixinName mixinName : this.contentType.getMetadata() )
            {
                mixinNamesBuilder.add( mixinName.toString() );
            }
        }
        this.metadataMixinNames = mixinNamesBuilder.build();
    }

    public String getName()
    {
        return contentType.getName() != null ? contentType.getName().toString() : null;
    }

    public String getDisplayName()
    {
        return contentType.getDisplayName();
    }

    public String getDescription()
    {
        return contentType.getDescription();
    }

    public Instant getCreatedTime()
    {
        return contentType.getCreatedTime();
    }

    public Instant getModifiedTime()
    {
        return contentType.getModifiedTime();
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public String getContentDisplayNameScript()
    {
        return contentType.getContentDisplayNameScript();
    }

    public String getSuperType()
    {
        return contentType.getSuperType() != null ? contentType.getSuperType().toString() : null;
    }

    public boolean isAbstract()
    {
        return contentType.isAbstract();
    }

    public boolean isFinal()
    {
        return contentType.isFinal();
    }

    public boolean isAllowChildContent()
    {
        return contentType.allowChildContent();
    }

    public String getCreator()
    {
        return contentType.getCreator() != null ? contentType.getCreator().toString() : null;
    }

    public String getModifier()
    {
        return contentType.getModifier() != null ? contentType.getModifier().toString() : null;
    }

    public List<String> getMetadata()
    {
        return metadataMixinNames;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
