package com.enonic.xp.content.page.region;

import com.enonic.xp.module.ModuleKey;

public interface ComponentService
{
    Component getByName( final ModuleKey module, final ComponentName name );
}
