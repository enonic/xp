package com.enonic.wem.portal.postprocess.inject;

import com.enonic.wem.portal.controller.JsContext;

public interface PostProcessInjection
{
    public enum Location
    {
        HEAD_TOP,
        HEAD_BOTTOM,
        BODY_TOP,
        BODY_BOTTOM
    }

    public String inject( JsContext context, Location location );
}
