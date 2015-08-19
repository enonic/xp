package com.enonic.xp.portal.script;

import com.google.common.annotations.Beta;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Beta
public interface PortalScriptService
{
    ScriptExports execute( ResourceKey script );
}
