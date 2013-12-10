package com.enonic.wem.api.command.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

public class CreateImageDescriptor
    extends Command<ImageDescriptor>
{
    private ImageDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private ModuleResourceKey controllerResource;

    private Form config;

    public CreateImageDescriptor()
    {
    }

    public CreateImageDescriptor key( final ImageDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreateImageDescriptor name( final ComponentDescriptorName name )
    {
        this.name = name;
        return this;
    }

    public CreateImageDescriptor displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateImageDescriptor controllerResource( final ModuleResourceKey controllerResource )
    {
        this.controllerResource = controllerResource;
        return this;
    }

    public CreateImageDescriptor config( final Form config )
    {
        this.config = config;
        return this;
    }

    public ImageDescriptorKey getKey()
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
