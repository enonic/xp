package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.ModuleResourceKey;

public class CreatePageDescriptor
    extends Command<PageDescriptor>
{
    private PageDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private ModuleResourceKey controllerResource;

    private Form config;

    public CreatePageDescriptor()
    {
    }

    public CreatePageDescriptor( final PageDescriptor pageDescriptor )
    {
        this.key = pageDescriptor.getKey();
        this.name = pageDescriptor.getName();
        this.displayName = pageDescriptor.getDisplayName();
        this.controllerResource = pageDescriptor.getControllerResource();
        this.config = pageDescriptor.getConfigForm();
    }

    public CreatePageDescriptor key( final PageDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreatePageDescriptor name( final String name )
    {
        this.name = new ComponentDescriptorName( name );
        return this;
    }

    public CreatePageDescriptor displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePageDescriptor controllerResource( final ModuleResourceKey controllerResource )
    {
        this.controllerResource = controllerResource;
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
