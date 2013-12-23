package com.enonic.wem.portal.postprocess;


import com.enonic.wem.portal.controller.JsHttpResponse;

public interface PostProcessor
{
    void processResponse( JsHttpResponse response )
        throws Exception;
}
