package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.module.ModuleKeys;

public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( final LayoutDescriptorKey key );

    LayoutDescriptor create( final CreateLayoutDescriptor command );

    LayoutDescriptors getByModules( final ModuleKeys moduleKeys );
}
