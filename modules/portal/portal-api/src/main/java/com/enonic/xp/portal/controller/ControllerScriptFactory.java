package com.enonic.xp.portal.controller;

import com.enonic.xp.resource.ResourceKey;

public interface ControllerScriptFactory
{
    ControllerScript fromDir( ResourceKey dir );

    ControllerScript fromScript( ResourceKey script );
}
