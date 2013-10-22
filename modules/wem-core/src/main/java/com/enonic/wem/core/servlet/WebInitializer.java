package com.enonic.wem.core.servlet;

import javax.servlet.ServletContext;

public interface WebInitializer
{
    public void initialize( ServletContext context );
}
