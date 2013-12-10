package com.enonic.wem.portal.controller;

import java.util.Set;

public interface JsController
{
    public Set<String> getMethods();

    public boolean execute( JsContext context );
}
