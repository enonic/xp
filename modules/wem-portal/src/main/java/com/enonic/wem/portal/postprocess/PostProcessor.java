package com.enonic.wem.portal.postprocess;


import com.enonic.wem.portal.controller.JsContext;

public interface PostProcessor
{
    void processResponse( JsContext context );
}
