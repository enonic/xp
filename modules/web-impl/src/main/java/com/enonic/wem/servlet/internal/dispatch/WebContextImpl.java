package com.enonic.wem.servlet.internal.dispatch;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.WebContext;
import com.enonic.xp.web.servlet.ServletRequestHolder;

public final class WebContextImpl
    implements WebContext
{
    private HttpServletRequest req;

    private HttpServletResponse res;

    @Override
    public String getMethod()
    {
        return this.req.getMethod();
    }

    @Override
    public boolean isGet()
    {
        return getMethod().equals( "GET" );
    }

    @Override
    public boolean isPost()
    {
        return getMethod().equals( "POST" );
    }

    @Override
    public String getPath()
    {
        return this.req.getPathInfo();
    }

    @Override
    public HttpServletRequest getRequest()
    {
        return this.req;
    }

    @Override
    public HttpServletResponse getResponse()
    {
        return this.res;
    }

    @Override
    public ServletContext getServletContext()
    {
        return this.req.getServletContext();
    }

    @Override
    public void setRequest( final HttpServletRequest req )
    {
        this.req = req;
        ServletRequestHolder.setRequest( this.req );
    }

    @Override
    public void setResponse( final HttpServletResponse res )
    {
        this.res = res;
    }
}
