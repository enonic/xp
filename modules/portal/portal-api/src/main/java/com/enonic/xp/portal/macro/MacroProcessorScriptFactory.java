package com.enonic.xp.portal.macro;

import com.enonic.xp.resource.ResourceKey;

public interface MacroProcessorScriptFactory
{
    MacroProcessor fromDir( ResourceKey dir );

    MacroProcessor fromScript( ResourceKey script );
}
