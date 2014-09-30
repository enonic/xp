package com.enonic.wem.portal.postprocess;

import com.enonic.wem.portal.PortalContext;

public interface PostProcessInjection
{
    public enum Tag
    {
        HEAD_BEGIN,
        HEAD_END,
        BODY_BEGIN,
        BODY_END
    }

    public String inject( PortalContext context, Tag tag );
}
