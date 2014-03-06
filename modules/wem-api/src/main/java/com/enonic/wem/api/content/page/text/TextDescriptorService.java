package com.enonic.wem.api.content.page.text;

import com.enonic.wem.api.module.ModuleKeys;

public interface TextDescriptorService
{
    TextDescriptor getByKey( final TextDescriptorKey key );

    TextDescriptors getByModules( final ModuleKeys moduleKeys );
}
