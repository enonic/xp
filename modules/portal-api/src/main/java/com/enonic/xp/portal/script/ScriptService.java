package com.enonic.xp.portal.script;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptService
{
    public ScriptExports execute( ResourceKey script );
}
