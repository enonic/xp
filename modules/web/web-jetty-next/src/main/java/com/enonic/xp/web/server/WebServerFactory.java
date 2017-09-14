package com.enonic.xp.web.server;

public interface WebServerFactory
{
    WebServer create( WebServerConfig config );
}
