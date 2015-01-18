package com.enonic.wem.api.content.page.region;

import com.enonic.wem.api.module.ModuleKey;

public interface ComponentService
{
    Component getByName( final ModuleKey module, final ComponentName name );
}
