package com.enonic.wem.portal.controller;

import javax.ws.rs.core.Response;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleKeyResolver;

public interface JsController
{
    public JsController scriptDir( ModuleResourceKey dir );

    public JsController context( JsContext context );

    public JsController moduleKeyResolver( ModuleKeyResolver moduleKeyResolver );

    public Response execute();
}
