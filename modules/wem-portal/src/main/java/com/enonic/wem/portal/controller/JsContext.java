package com.enonic.wem.portal.controller;

public final class JsContext
{
    private JsHttpRequest request;

    private JsHttpResponse response;

    public JsHttpRequest getRequest()
    {
        return this.request;
    }

    public void setRequest( final JsHttpRequest request )
    {
        this.request = request;
    }

    public JsHttpResponse getResponse()
    {
        return this.response;
    }

    public void setResponse( final JsHttpResponse response )
    {
        this.response = response;
    }
}
