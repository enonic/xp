package com.enonic.wem.portal.postprocess;

import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.postprocess.instruction.PostProcessInstruction;

public final class TestPostProcessInstruction
    implements PostProcessInstruction
{
    @Override
    public String evaluate( final JsContext context, final String instruction )
    {
        return instruction;
    }
}
