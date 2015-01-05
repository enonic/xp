package com.enonic.xp.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebContext
{
    public String getMethod();

    public boolean isGet();

    public boolean isPost();

    public String getPath();

    public HttpServletRequest getRequest();

    public HttpServletResponse getResponse();

    public ServletContext getServletContext();

    public void setRequest( HttpServletRequest req );

    public void setResponse( HttpServletResponse res );
}
