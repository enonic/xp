package com.enonic.wem.portal.controller;

import javax.ws.rs.core.MultivaluedMap;

import com.enonic.wem.api.rendering.RenderingMode;

public interface JsHttpRequest
{
    public String getMethod();

    public MultivaluedMap<String, String> getParams();

    public RenderingMode getMode();
}
