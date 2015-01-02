package com.enonic.wem.api.content.page;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;

public interface PageDescriptorService
{
    PageDescriptor getByKey( final DescriptorKey key );

    PageDescriptors getByModule( final ModuleKey moduleKey );

    PageDescriptors getByModules( final ModuleKeys moduleKeys );

}
