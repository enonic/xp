package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;

public final class ProcessCreateResult
{
    private final ContentIds processedIds;

    private final CreateContentParams createContentParams;

    public ProcessCreateResult( final CreateContentParams createContentParams, final ContentIds processedIds )
    {
        this.processedIds = processedIds;
        this.createContentParams = createContentParams;
    }

    public ContentIds getProcessedIds()
    {
        return processedIds;
    }

    public CreateContentParams getCreateContentParams()
    {
        return createContentParams;
    }
}
