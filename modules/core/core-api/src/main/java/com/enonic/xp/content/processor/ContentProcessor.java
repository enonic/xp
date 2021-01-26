package com.enonic.xp.content.processor;

import com.enonic.xp.schema.content.ContentType;

public interface ContentProcessor
{
    boolean supports( ContentType contentType );

    ProcessCreateResult processCreate( ProcessCreateParams params );

    ProcessUpdateResult processUpdate( ProcessUpdateParams params );
}
