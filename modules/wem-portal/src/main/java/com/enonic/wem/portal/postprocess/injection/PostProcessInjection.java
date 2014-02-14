package com.enonic.wem.portal.postprocess.injection;

import com.enonic.wem.portal.controller.JsContext;

public interface PostProcessInjection
{
    public enum Tag
    {
        HEAD_BEGIN,
        HEAD_END,
        BODY_BEGIN,
        BODY_END
    }

    public String inject( JsContext context, Tag tag );
}
