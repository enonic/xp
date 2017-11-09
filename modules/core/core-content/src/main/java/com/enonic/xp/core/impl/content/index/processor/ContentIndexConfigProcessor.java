package com.enonic.xp.core.impl.content.index.processor;

import com.enonic.xp.index.PatternIndexConfigDocument;

public interface ContentIndexConfigProcessor
{
    PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder );
}
