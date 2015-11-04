package com.enonic.xp.portal.impl.error;

import com.enonic.xp.resource.ResourceKey;

public interface ErrorHandlerScriptFactory
{
    ErrorHandlerScript errorScript( ResourceKey script );
}
