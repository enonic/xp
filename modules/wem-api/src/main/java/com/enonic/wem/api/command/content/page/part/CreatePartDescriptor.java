package com.enonic.wem.api.command.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

public class CreatePartDescriptor
    extends Command<PartDescriptor>
{
    private PartDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private ModuleResourceKey controllerResource;

    private Form config;

    public CreatePartDescriptor()
    {
    }

    public CreatePartDescriptor( final PartDescriptor partDescriptor )
    {
        this.key = partDescriptor.getKey();
        this.name = partDescriptor.getName();
        this.displayName = partDescriptor.getDisplayName();
        this.controllerResource = partDescriptor.getControllerResource();
        this.config = partDescriptor.getConfigForm();
    }

    public CreatePartDescriptor key( final PartDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreatePartDescriptor name( final String name )
    {
        this.name = new ComponentDescriptorName( name );
        return this;
    }

    public CreatePartDescriptor displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePartDescriptor controllerResource( final ModuleResourceKey controllerResource )
    {
        this.controllerResource = controllerResource;
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

    public ModuleResourceKey getControllerResource()
    {
        return controllerResource;
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
