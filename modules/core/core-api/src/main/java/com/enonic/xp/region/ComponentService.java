package com.enonic.xp.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;

@Beta
public interface ComponentService
{
    Component getByName( final ApplicationKey applicationKey, final ComponentName name );
}
