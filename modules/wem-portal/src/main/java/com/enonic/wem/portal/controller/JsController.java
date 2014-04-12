package com.enonic.wem.portal.controller;

import javax.ws.rs.core.Response;

import com.enonic.wem.api.resource.ResourceKey;

public interface JsController
{
    public JsController scriptDir( ResourceKey dir );

    public JsController context( JsContext context );

    public Response execute();
}
