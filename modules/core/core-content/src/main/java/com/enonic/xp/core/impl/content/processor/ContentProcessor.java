package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.schema.content.ContentType;

public interface ContentProcessor
{
    boolean supports( final ContentType contentType );

    ProcessCreateResult processCreate( final ProcessCreateParams params );

    ProcessUpdateResult processUpdate( final ProcessUpdateParams params );
}
