package com.enonic.xp.portal.postprocess;

import com.enonic.xp.portal.PortalContext;

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
