package com.enonic.xp.web.dispatch;

import javax.servlet.Servlet;

public interface ApiServlet
    extends Servlet
{
    void setServlet( Servlet servlet );
}
