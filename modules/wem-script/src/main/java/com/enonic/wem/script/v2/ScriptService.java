package com.enonic.wem.script.v2;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptService
{
    public ScriptExports execute( ResourceKey script );
}
