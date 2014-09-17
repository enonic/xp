package com.enonic.wem.api.content.page;

import com.enonic.wem.api.module.ModuleKeys;

public interface PageDescriptorService
{
    PageDescriptor getByKey( final PageDescriptorKey key );

    PageDescriptors getByModules( final ModuleKeys moduleKeys );

}
