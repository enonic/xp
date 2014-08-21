package com.enonic.wem.portal.internal.postprocess.instruction;

import com.enonic.wem.portal.internal.controller.JsContext;

public interface PostProcessInstruction
{
    public String evaluate( JsContext context, String instruction );
}
