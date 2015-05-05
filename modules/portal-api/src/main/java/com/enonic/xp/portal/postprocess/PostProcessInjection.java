package com.enonic.xp.portal.postprocess;

import java.util.List;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalContext;

@Beta
public interface PostProcessInjection
{
    enum Tag
    {
        HEAD_BEGIN,
        HEAD_END,
        BODY_BEGIN,
        BODY_END
    }

    List<String> inject( PortalContext context, Tag tag );
}
