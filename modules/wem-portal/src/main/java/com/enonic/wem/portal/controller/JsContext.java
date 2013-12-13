package com.enonic.wem.portal.controller;

import com.enonic.wem.portal.content.JsContextContent;

public final class JsContext
{
    private JsHttpRequest request;

    private JsHttpResponse response;

    private JsContextContent content;

    public JsContext()
    {
        this.response = new JsHttpResponse();
    }

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

    public JsContextContent getContent()
    {
        return content;
    }

    public void setContent( final JsContextContent content )
    {
        this.content = content;
    }
}
