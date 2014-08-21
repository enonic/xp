package com.enonic.wem.portal.internal.postprocess;

import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.postprocess.instruction.PostProcessInstruction;

public final class TestPostProcessInstruction
    implements PostProcessInstruction
{
    @Override
    public String evaluate( final JsContext context, final String instruction )
    {
        return instruction;
    }
}
