package com.enonic.wem.portal.controller;

import javax.ws.rs.core.Response;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface JsController
{
    public JsController scriptDir( ModuleResourceKey dir );

    public JsController context( JsContext context );

    public Response execute();
}
