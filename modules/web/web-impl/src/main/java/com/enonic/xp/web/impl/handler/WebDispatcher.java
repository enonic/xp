package com.enonic.xp.web.impl.handler;

import java.util.List;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;

public interface WebDispatcher
{
    void add( WebHandler webHandler );

    void remove( WebHandler webHandler );

    WebResponse dispatch( WebRequest req, WebResponse res )
        throws Exception;

    List<WebHandler> list();
}
