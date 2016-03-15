package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;

public class ProcessUpdateParams
{
    private final UpdateContentParams updateContentParams;

    private final MediaInfo mediaInfo;

    private final ContentType contentType;

    public ProcessUpdateParams( final UpdateContentParams updateContentParams, final MediaInfo mediaInfo, final ContentType contentType )
    {
        this.updateContentParams = updateContentParams;
        this.mediaInfo = mediaInfo;
        this.contentType = contentType;
    }

    public UpdateContentParams getUpdateContentParams()
    {
        return updateContentParams;
    }

    public MediaInfo getMediaInfo()
    {
        return mediaInfo;
    }

    public ContentType getContentType()
    {
        return contentType;
    }
}
