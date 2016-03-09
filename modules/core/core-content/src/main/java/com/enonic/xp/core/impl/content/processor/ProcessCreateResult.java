package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.CreateContentParams;

public class ProcessCreateResult
{

    private final CreateContentParams createContentParams;

    public ProcessCreateResult( final CreateContentParams createContentParams )
    {
        this.createContentParams = createContentParams;
    }

    public CreateContentParams getCreateContentParams()
    {
        return createContentParams;
    }
}
