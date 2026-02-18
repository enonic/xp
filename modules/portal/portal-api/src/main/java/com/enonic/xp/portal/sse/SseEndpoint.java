package com.enonic.xp.portal.sse;

import com.enonic.xp.web.sse.SseConfig;

public interface SseEndpoint
{
    void onEvent( SseEvent event );

    SseConfig getConfig();
}
