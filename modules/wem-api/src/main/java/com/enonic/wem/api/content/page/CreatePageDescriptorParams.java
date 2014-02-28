package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.region.RegionDescriptors;
import com.enonic.wem.api.form.Form;

public class CreatePageDescriptorParams
    extends Command<PageDescriptor>
{
    private PageDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private Form config;

    private RegionDescriptors regions;

    public CreatePageDescriptorParams()
    {
    }

    public CreatePageDescriptorParams key( final PageDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreatePageDescriptorParams name( final ComponentDescriptorName name )
    {
        this.name = name;
        return this;
    }

    public CreatePageDescriptorParams name( final String name )
    {
        this.name = new ComponentDescriptorName( name );
        return this;
    }

    public CreatePageDescriptorParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePageDescriptorParams config( final Form config )
    {
        this.config = config;
        return this;
    }

    public CreatePageDescriptorParams regions( final RegionDescriptors value )
    {
        this.regions = value;
        return this;
    }

    public PageDescriptorKey getKey()
    {
        return key;
    }

    public ComponentDescriptorName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Form getConfig()
    {
        return config;
    }

    public RegionDescriptors getRegions()
    {
        return regions;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
        Preconditions.checkNotNull( name, "name is required" );
        Preconditions.checkNotNull( displayName, "displayName is required" );
        Preconditions.checkNotNull( regions, "regions are required" );
        Preconditions.checkNotNull( config, "config is required" );
    }
}
