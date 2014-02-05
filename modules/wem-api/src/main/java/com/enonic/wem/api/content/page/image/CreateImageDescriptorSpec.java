package com.enonic.wem.api.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.form.Form;

public final class CreateImageDescriptorSpec
{
    private ImageDescriptorKey key;

    private ComponentDescriptorName name;

    private String displayName;

    private Form config;

    public static CreateImageDescriptorSpec fromImageDescriptor( final ImageDescriptor imageDescriptor )
    {
        final CreateImageDescriptorSpec createModule = new CreateImageDescriptorSpec();
        createModule.key = imageDescriptor.getKey();
        createModule.name = imageDescriptor.getName();
        createModule.displayName = imageDescriptor.getDisplayName();
        createModule.config = imageDescriptor.getConfigForm();
        return createModule;
    }

    public CreateImageDescriptorSpec key( final ImageDescriptorKey key )
    {
        this.key = key;
        return this;
    }

    public CreateImageDescriptorSpec name( final ComponentDescriptorName name )
    {
        this.name = name;
        return this;
    }

    public CreateImageDescriptorSpec displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateImageDescriptorSpec config( final Form config )
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

    public Form getConfig()
    {
        return config;
    }


    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
        Preconditions.checkNotNull( name, "name is required" );
        Preconditions.checkNotNull( displayName, "displayName is required" );
    }
}
