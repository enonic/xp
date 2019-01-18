package com.enonic.xp.web.jetty.impl;

import java.util.List;

import javax.servlet.ServletContext;

public interface JettyController
{
    List<ServletContext> getServletContexts();
}
