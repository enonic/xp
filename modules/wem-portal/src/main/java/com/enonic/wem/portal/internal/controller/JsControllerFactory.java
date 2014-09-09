package com.enonic.wem.portal.internal.controller;

import com.enonic.wem.api.resource.ResourceKey;

public interface JsControllerFactory
{
    public JsController newController( ResourceKey scriptDir );
}
