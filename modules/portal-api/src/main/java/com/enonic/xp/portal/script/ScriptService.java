package com.enonic.xp.portal.script;

import com.google.common.annotations.Beta;

import com.enonic.xp.resource.ResourceKey;

@Beta
public interface ScriptService
{
    ScriptExports execute( ResourceKey script );
}
