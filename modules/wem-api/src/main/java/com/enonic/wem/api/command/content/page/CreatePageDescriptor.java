package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.form.Form;

public class CreatePageDescriptor
    extends Command<PageDescriptor>
{
    private PageDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private Form config;

    public CreatePageDescriptor()
    {
    }

    public CreatePageDescriptor key( final PageDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreatePageDescriptor name( final ComponentDescriptorName name )
    {
        this.name = name;
        return this;
    }

    public CreatePageDescriptor displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePageDescriptor config( final Form config )
    {
        this.config = config;
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

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
        Preconditions.checkNotNull( name, "name is required" );
        Preconditions.checkNotNull( displayName, "displayName is required" );
    }
}
