package com.enonic.xp.portal.impl.auth;

import com.enonic.xp.resource.ResourceKey;

public interface IdProviderControllerScriptFactory
{
    IdProviderControllerScript fromScript( ResourceKey script );
}
