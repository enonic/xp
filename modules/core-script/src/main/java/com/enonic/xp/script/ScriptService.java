package com.enonic.xp.script;

import com.enonic.xp.resource.ResourceKey;

public interface ScriptService
{
    ScriptExports execute( final ResourceKey script );
}
