package com.enonic.wem.portal.controller;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

public final class JsContext
{
    private JsHttpRequest request;

    private JsHttpResponse response;

    private Content content;

    private PortalUrlScriptBean portalUrlScriptBean;

    private PageComponent component;

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

    public PortalUrlScriptBean getUrl()
    {
        return this.portalUrlScriptBean;
    }

    public void setPortalUrlScriptBean( final PortalUrlScriptBean portalUrlScriptBean )
    {
        this.portalUrlScriptBean = portalUrlScriptBean;
    }

    public PageComponent getComponent()
    {
        return component;
    }

    public void setComponent( final PageComponent component )
    {
        this.component = component;
    }
}
