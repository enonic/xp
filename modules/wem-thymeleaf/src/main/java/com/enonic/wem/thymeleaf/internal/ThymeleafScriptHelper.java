package com.enonic.wem.thymeleaf.internal;

import com.enonic.wem.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.thymeleaf.ThymeleafRenderParams;

public final class ThymeleafScriptHelper
{
    private final ThymeleafProcessor processor;

    public ThymeleafScriptHelper( final ThymeleafProcessor processor )
    {
        this.processor = processor;
    }

    public ThymeleafProcessor getProcessor()
    {
        return this.processor;
    }

    public ThymeleafRenderParams newRenderParams()
    {
        return new ThymeleafRenderParams();
    }
}
