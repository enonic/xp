package com.enonic.wem.api.content.page;

import com.enonic.wem.api.module.ModuleKey;

public interface PageComponentService
{
    AbstractDescriptorBasedPageComponent<? extends DescriptorKey> getByName( final ModuleKey module, final ComponentName name );
}
