package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.media.MediaInfo;

public final class ProcessCreateParams
{
    private final CreateContentParams params;

    private final ContentIds processedReferences;

    private final MediaInfo mediaInfo;

    public ProcessCreateParams( final CreateContentParams params, final MediaInfo mediaInfo, final ContentIds processedReferences )
    {
        this.params = params;
        this.mediaInfo = mediaInfo;
        this.processedReferences = processedReferences;
    }

    public CreateContentParams getCreateContentParams()
    {
        return params;
    }

    public MediaInfo getMediaInfo()
    {
        return mediaInfo;
    }

    public ContentIds getProcessedReferences()
    {
        return processedReferences;
    }
}
