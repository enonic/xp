package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;

public final class ProcessCreateResult
{
    private final ContentIds processedReferences;

    private final CreateContentParams createContentParams;

    public ProcessCreateResult( final CreateContentParams createContentParams, final ContentIds processedReferences )
    {
        this.processedReferences = processedReferences;
        this.createContentParams = createContentParams;
    }

    public ContentIds getProcessedReferences()
    {
        return processedReferences;
    }

    public CreateContentParams getCreateContentParams()
    {
        return createContentParams;
    }
}
