package com.enonic.xp.web.impl.handler;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;

public interface WebDispatcher
    extends Iterable<WebHandler>
{
    void add( WebHandler webHandler );

    void remove( WebHandler webHandler );

    WebResponse dispatch( WebRequest req, WebResponse res )
        throws Exception;
}
