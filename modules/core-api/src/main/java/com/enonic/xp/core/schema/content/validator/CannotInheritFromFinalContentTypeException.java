package com.enonic.xp.core.schema.content.validator;

import com.enonic.xp.core.schema.content.ContentType;

public final class CannotInheritFromFinalContentTypeException
    extends InvalidContentTypeException
{
    CannotInheritFromFinalContentTypeException( final ContentType contentType )
    {
        super( contentType );
    }
}
