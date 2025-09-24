package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.Content;


public final class ProcessUpdateResult
{
    private final Content content;

    public ProcessUpdateResult( final Content content )
    {
        this.content = content;
    }

    public Content getContent()
    {
        return content;
    }
}
