package com.enonic.wem.api.command.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

public class CreateLayoutDescriptor
    extends Command<LayoutDescriptor>
{
    private LayoutDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private ModuleResourceKey controllerResource;

    private Form config;

    public CreateLayoutDescriptor()
    {
    }

    public CreateLayoutDescriptor( final LayoutDescriptor layoutDescriptor )
    {
        this.key = layoutDescriptor.getKey();
        this.name = layoutDescriptor.getName();
        this.displayName = layoutDescriptor.getDisplayName();
        this.controllerResource = layoutDescriptor.getControllerResource();
        this.config = layoutDescriptor.getConfigForm();
    }

    public CreateLayoutDescriptor key( final LayoutDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreateLayoutDescriptor name( final String name )
    {
        this.name = new ComponentDescriptorName( name );
        return this;
    }

    public CreateLayoutDescriptor displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateLayoutDescriptor controllerResource( final ModuleResourceKey controllerResource )
    {
        this.controllerResource = controllerResource;
        return this;
    }

    public CreateLayoutDescriptor config( final Form config )
    {
        this.config = config;
        return this;
    }

    public LayoutDescriptorKey getKey()
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
