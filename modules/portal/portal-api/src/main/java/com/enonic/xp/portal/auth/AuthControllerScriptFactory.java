package com.enonic.xp.portal.auth;

import com.enonic.xp.resource.ResourceKey;

public interface AuthControllerScriptFactory
{
    AuthControllerScript fromScript( ResourceKey script );
}
