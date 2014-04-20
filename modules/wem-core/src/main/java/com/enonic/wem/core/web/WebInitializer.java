package com.enonic.wem.core.web;

import javax.servlet.ServletContext;

public interface WebInitializer
{
    public void initialize( ServletContext context );
}
