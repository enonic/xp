package com.enonic.xp.portal.script;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

@PublicApi
public interface PortalScriptService
{
    boolean hasScript( ResourceKey script );

    ScriptExports execute( ResourceKey script );

    ScriptValue toScriptValue( ResourceKey script, Object value );

    Object toNativeObject( ResourceKey script, Object value );
}
