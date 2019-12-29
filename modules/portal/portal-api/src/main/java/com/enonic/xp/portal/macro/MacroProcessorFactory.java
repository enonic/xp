package com.enonic.xp.portal.macro;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public interface MacroProcessorFactory
{
    MacroProcessor fromDir( ResourceKey dir );

    MacroProcessor fromScript( ResourceKey script );
}
