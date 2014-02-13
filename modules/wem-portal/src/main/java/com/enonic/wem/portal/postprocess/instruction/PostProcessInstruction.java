package com.enonic.wem.portal.postprocess.instruction;

import com.enonic.wem.portal.controller.JsContext;

public interface PostProcessInstruction
{
    public String evaluate( JsContext context, String instruction );
}
