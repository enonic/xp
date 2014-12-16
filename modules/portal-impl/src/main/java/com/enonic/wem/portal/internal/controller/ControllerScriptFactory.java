package com.enonic.wem.portal.internal.controller;

import com.enonic.wem.api.resource.ResourceKey;

public interface ControllerScriptFactory
{
    public ControllerScript newController( ResourceKey scriptDir );
}
