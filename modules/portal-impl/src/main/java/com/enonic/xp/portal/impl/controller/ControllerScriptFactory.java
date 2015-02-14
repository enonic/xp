package com.enonic.xp.portal.impl.controller;

import com.enonic.xp.core.resource.ResourceKey;

public interface ControllerScriptFactory
{
    public ControllerScript newController( ResourceKey scriptDir );
}
