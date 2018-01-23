package com.enonic.xp.admin.impl.json.content.page;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.region.ComponentDescriptor;


public abstract class DescriptorJson
    implements ItemJson
{
    private final ComponentDescriptor descriptor;

    private final FormJson configJson;

    private final LocaleMessageResolver localeMessageResolver;

    public DescriptorJson( final ComponentDescriptor descriptor, final LocaleMessageResolver localeMessageResolver )
    {
        Preconditions.checkNotNull( descriptor );
        Preconditions.checkNotNull( localeMessageResolver );

        this.localeMessageResolver = localeMessageResolver;
        this.descriptor = descriptor;

        this.configJson = new FormJson( descriptor.getConfig(), localeMessageResolver );
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

    public FormJson getConfig()
    {
        return configJson;
    }

}
