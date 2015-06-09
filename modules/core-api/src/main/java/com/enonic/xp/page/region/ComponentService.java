package com.enonic.xp.page.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleKey;

@Beta
public interface ComponentService
{
    Component getByName( final ModuleKey module, final ComponentName name );
}
