package com.enonic.xp.core.content.page.region;

import com.enonic.xp.core.module.ModuleKey;

public interface ComponentService
{
    Component getByName( final ModuleKey module, final ComponentName name );
}
