package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public interface ComponentService
{
    Component getByName( final ApplicationKey applicationKey, final ComponentName name );
}
