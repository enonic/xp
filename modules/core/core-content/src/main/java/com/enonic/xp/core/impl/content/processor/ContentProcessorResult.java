package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.schema.mixin.Mixins;

public class ContentProcessorResult
{
    private final Mixins mixins;

    private final ExtraDatas extraDatas;

    public ContentProcessorResult( final ExtraDatas extraDatas, final Mixins mixins )
    {
        this.extraDatas = extraDatas;
        this.mixins = mixins;
    }
}
