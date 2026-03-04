package com.enonic.xp.portal.macro;

import com.enonic.xp.resource.ResourceKey;


public interface MacroProcessorFactory
{
    MacroProcessor fromDir( ResourceKey dir );

    MacroProcessor fromScript( ResourceKey script );
}
