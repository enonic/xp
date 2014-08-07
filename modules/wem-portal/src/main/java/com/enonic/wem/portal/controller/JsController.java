package com.enonic.wem.portal.controller;

import com.enonic.wem.api.resource.ModuleResourceKey;

public interface JsController
{
    public JsController scriptDir( ModuleResourceKey dir );

    public JsController context( JsContext context );

    public void execute();
}
