package com.enonic.wem.portal.controller;

import java.nio.file.Path;

public interface JsControllerFactory
{
    public JsController newController( Path path );
}
