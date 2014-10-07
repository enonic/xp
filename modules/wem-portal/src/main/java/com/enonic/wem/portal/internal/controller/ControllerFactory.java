package com.enonic.wem.portal.internal.controller;

import com.enonic.wem.api.resource.ResourceKey;

public interface ControllerFactory
{
    public Controller newController( ResourceKey scriptDir );
}
