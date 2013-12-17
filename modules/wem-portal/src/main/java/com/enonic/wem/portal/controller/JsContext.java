package com.enonic.wem.portal.controller;

import com.enonic.wem.api.content.Content;

public final class JsContext
{
    private JsHttpRequest request;

    private JsHttpResponse response;

    private Content content;

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

    public Content getContent()
    {
        return content;
    }

    public void setContent( final Content content )
    {
        this.content = content;
    }
}
