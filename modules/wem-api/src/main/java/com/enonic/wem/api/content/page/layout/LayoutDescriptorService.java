package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;

public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( final LayoutDescriptorKey key );

    LayoutDescriptors getByModule( final ModuleKey moduleKey );

    LayoutDescriptors getByModules( final ModuleKeys moduleKeys );
}
