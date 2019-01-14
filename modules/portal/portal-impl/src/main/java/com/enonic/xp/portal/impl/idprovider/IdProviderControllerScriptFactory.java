package com.enonic.xp.portal.impl.idprovider;

import com.enonic.xp.resource.ResourceKey;

public interface IdProviderControllerScriptFactory
{
    IdProviderControllerScript fromScript( ResourceKey script );
}
