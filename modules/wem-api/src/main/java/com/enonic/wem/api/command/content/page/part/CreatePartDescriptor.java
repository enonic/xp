package com.enonic.wem.api.command.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.form.Form;

public class CreatePartDescriptor
    extends Command<PartDescriptor>
{
    private PartDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private Form config;

    public CreatePartDescriptor()
    {
    }

    public CreatePartDescriptor key( final PartDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreatePartDescriptor name( final ComponentDescriptorName name )
    {
        this.name = name;
        return this;
    }

    public CreatePartDescriptor displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePartDescriptor config( final Form config )
    {
        this.config = config;
        return this;
    }

    public PartDescriptorKey getKey()
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
