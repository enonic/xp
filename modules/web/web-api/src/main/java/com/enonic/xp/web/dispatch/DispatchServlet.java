package com.enonic.xp.web.dispatch;

import javax.servlet.Servlet;

public interface DispatchServlet
    extends Servlet
{
    String getConnector();
}
