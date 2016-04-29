package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.media.MediaInfo;

public class ProcessCreateParams
{

    private final CreateContentParams params;

    private final MediaInfo mediaInfo;

    public ProcessCreateParams( final CreateContentParams params, final MediaInfo mediaInfo )
    {
        this.params = params;
        this.mediaInfo = mediaInfo;
    }

    public CreateContentParams getCreateContentParams()
    {
        return params;
    }

    public MediaInfo getMediaInfo()
    {
        return mediaInfo;
    }
}
