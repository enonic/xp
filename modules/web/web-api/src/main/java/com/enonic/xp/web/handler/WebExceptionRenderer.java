package com.enonic.xp.web.handler;

public interface WebExceptionRenderer
{
    WebResponse render( WebRequest req, WebException cause );
}
