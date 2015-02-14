package com.enonic.xp.portal.impl.controller;

import com.enonic.wem.api.resource.ResourceKey;

public interface ControllerScriptFactory
{
    public ControllerScript newController( ResourceKey scriptDir );
}
