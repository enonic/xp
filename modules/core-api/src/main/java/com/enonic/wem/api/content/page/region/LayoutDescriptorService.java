package com.enonic.wem.api.content.page.region;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;

public interface LayoutDescriptorService
{
    LayoutDescriptor getByKey( final DescriptorKey key );

    LayoutDescriptors getByModule( final ModuleKey moduleKey );

    LayoutDescriptors getByModules( final ModuleKeys moduleKeys );
}
