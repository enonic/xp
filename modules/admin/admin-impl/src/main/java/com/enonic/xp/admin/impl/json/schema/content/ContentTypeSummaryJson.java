package com.enonic.xp.admin.impl.json.schema.content;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.ChangeTraceableJson;
import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.xdata.XDataName;

@SuppressWarnings("UnusedDeclaration")
public class ContentTypeSummaryJson
    implements ItemJson, ChangeTraceableJson
{
    private final ContentType contentType;

    private final String iconUrl;

    private final LocaleMessageResolver localeMessageResolver;

    private final ImmutableList<String> metadataMixinNames;

    public ContentTypeSummaryJson( final ContentType contentType, final ContentTypeIconUrlResolver iconUrlResolver,
                                   final LocaleMessageResolver localeMessageResolver )
    {
        this.contentType = contentType;
        this.localeMessageResolver = localeMessageResolver;
        this.iconUrl = iconUrlResolver.resolve( contentType );

        ImmutableList.Builder<String> xDataNamesBuilder = new ImmutableList.Builder<>();
        if ( this.contentType.getXData() != null )
        {
            for ( XDataName xDataName : this.contentType.getXData() )
            {
                xDataNamesBuilder.add( xDataName.toString() );
            }
        }
        this.metadataMixinNames = xDataNamesBuilder.build();
    }

    public String getName()
    {
        return contentType.getName() != null ? contentType.getName().toString() : null;
    }

    public String getDisplayName()
    {
        if ( StringUtils.isNotBlank( contentType.getDisplayNameI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( contentType.getDisplayNameI18nKey(), contentType.getDisplayName() );
        }
        else
        {
            return contentType.getDisplayName();
        }
    }

    public String getDescription()
    {
        if ( StringUtils.isNotBlank( contentType.getDescriptionI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( contentType.getDescriptionI18nKey(), contentType.getDescription() );
        }
        else
        {
            return contentType.getDescription();
        }
    }

    public String getDisplayNameLabel()
    {
        if ( StringUtils.isNotBlank( contentType.getDisplayNameLabelI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( contentType.getDisplayNameLabelI18nKey(), contentType.getDisplayNameLabel() );
        }
        else
        {
            return contentType.getDisplayNameLabel();
        }
    }

    @Override
    public Instant getCreatedTime()
    {
        return contentType.getCreatedTime();
    }

    @Override
    public Instant getModifiedTime()
    {
        return contentType.getModifiedTime();
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public String getDisplayNameExpression()
    {
        return contentType.getDisplayNameExpression();
    }

    public String getSuperType()
    {
        return contentType.getSuperType() != null ? contentType.getSuperType().toString() : null;
    }

    public List<String> getMetadata()
    {
        return metadataMixinNames;
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

    @Override
    public String getCreator()
    {
        return contentType.getCreator() != null ? contentType.getCreator().toString() : null;
    }

    @Override
    public String getModifier()
    {
        return contentType.getModifier() != null ? contentType.getModifier().toString() : null;
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
