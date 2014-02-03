package com.enonic.wem.web;

import javax.servlet.ServletContext;

public interface WebInitializer
{
    public void initialize( ServletContext context );
}
