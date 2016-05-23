package com.enonic.xp.portal.impl.auth;

import com.enonic.xp.resource.ResourceKey;

public interface AuthControllerScriptFactory
{
    AuthControllerScript fromScript( ResourceKey script );
}
