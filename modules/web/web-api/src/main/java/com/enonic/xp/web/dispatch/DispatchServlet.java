package com.enonic.xp.web.dispatch;

import jakarta.servlet.Servlet;

public interface DispatchServlet
    extends Servlet
{
    String getConnector();
}
