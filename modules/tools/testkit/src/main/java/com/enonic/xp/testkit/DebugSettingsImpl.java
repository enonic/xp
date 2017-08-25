package com.enonic.xp.testkit;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.runtime.DebugSettings;

final class DebugSettingsImpl
    implements DebugSettings
{
    @Override
    public String scriptName( final Resource resource )
    {
        final String str = resource.getKey().getPath();
        if ( str.startsWith( "/" ) )
        {
            return str.substring( 1 );
        }
        else
        {
            return str;
        }
    }
}
