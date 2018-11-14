package com.enonic.xp.content.processor;

import com.enonic.xp.schema.content.ContentType;

public interface ContentProcessor
{
    boolean supports( final ContentType contentType );

    ProcessCreateResult processCreate( final ProcessCreateParams params );

    ProcessUpdateResult processUpdate( final ProcessUpdateParams params );
}
