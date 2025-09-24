package com.enonic.xp.core.impl.content.processor;

import com.enonic.xp.schema.content.ContentTypeName;

public interface ContentProcessor
{
    boolean supports( ContentTypeName contentType );

    ProcessCreateResult processCreate( ProcessCreateParams params );

    ProcessUpdateResult processUpdate( ProcessUpdateParams params );
}
