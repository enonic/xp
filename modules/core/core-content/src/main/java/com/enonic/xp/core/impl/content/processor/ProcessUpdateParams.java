package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.media.MediaInfo;

public class ProcessUpdateParams
{
    private final UpdateContentParams updateContentParams;

    private final MediaInfo mediaInfo;

    public ProcessUpdateParams( final UpdateContentParams updateContentParams, final MediaInfo mediaInfo )
    {
        this.updateContentParams = updateContentParams;
        this.mediaInfo = mediaInfo;
    }

    public UpdateContentParams getUpdateContentParams()
    {
        return updateContentParams;
    }

    public MediaInfo getMediaInfo()
    {
        return mediaInfo;
    }
}
