package com.enonic.xp.admin.impl.json.content.page;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.region.ComponentDescriptor;


public abstract class DescriptorJson
    implements ItemJson
{
    private final ComponentDescriptor descriptor;

    private final FormJson configJson;

    private final LocaleMessageResolver localeMessageResolver;

    private final boolean editable;

    private final boolean deletable;

    public DescriptorJson( final ComponentDescriptor descriptor, final LocaleMessageResolver localeMessageResolver,
                           final InlineMixinResolver inlineMixinResolver )
    {
        Preconditions.checkNotNull( descriptor );
        Preconditions.checkNotNull( localeMessageResolver );

        this.editable = false;
        this.deletable = false;

        this.localeMessageResolver = localeMessageResolver;
        this.descriptor = descriptor;

        this.configJson = new FormJson( descriptor.getConfig(), localeMessageResolver, inlineMixinResolver );
    }

    public String getKey()
    {
        return descriptor.getKey().toString();
    }

    public String getName()
    {
        return descriptor.getName() != null ? descriptor.getName().toString() : null;
    }

    public String getDisplayName()
    {
        if ( StringUtils.isNotBlank( descriptor.getDisplayNameI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( descriptor.getDisplayNameI18nKey(), descriptor.getDisplayName() );
        }
        else
        {
            return descriptor.getDisplayName();
        }
    }

    public String getDescription()
    {
        if ( StringUtils.isNotBlank( descriptor.getDescriptionI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( descriptor.getDescriptionI18nKey(), descriptor.getDescription() );
        }
        else
        {
            return descriptor.getDescription();
        }
    }

    public FormJson getConfig()
    {
        return configJson;
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
