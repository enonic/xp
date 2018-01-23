package com.enonic.xp.admin.impl.json.content.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.json.content.page.region.RegionDescriptorJson;
import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;

public class PageDescriptorJson
    implements ItemJson
{
    private final PageDescriptor descriptor;

    private final FormJson configJson;

    private final boolean editable;

    private final boolean deletable;

    private final List<RegionDescriptorJson> regionsJson;

    private final LocaleMessageResolver localeMessageResolver;

    public PageDescriptorJson( final PageDescriptor descriptor, final LocaleMessageResolver localeMessageResolver )
    {
        Preconditions.checkNotNull( descriptor );
        Preconditions.checkNotNull( localeMessageResolver );

        this.descriptor = descriptor;
        this.localeMessageResolver = localeMessageResolver;
        this.configJson = new FormJson( descriptor.getConfig(), localeMessageResolver );

        this.editable = false;
        this.deletable = false;

        final RegionDescriptors regions = descriptor.getRegions();
        this.regionsJson = new ArrayList<>( regions.numberOfRegions() );
        for ( final RegionDescriptor regionDescriptor : regions )
        {
            this.regionsJson.add( new RegionDescriptorJson( regionDescriptor ) );
        }
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

    public List<RegionDescriptorJson> getRegions()
    {
        return this.regionsJson;
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
