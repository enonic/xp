package com.enonic.wem.portal.internal.postprocess;


import com.enonic.wem.portal.internal.controller.JsContext;

public interface PostProcessor
{
    void processResponse( JsContext context );
}
