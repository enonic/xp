package com.enonic.xp.schema.content.validator;

import com.enonic.xp.schema.content.ContentType;

public final class CannotInheritFromFinalContentTypeException
    extends InvalidContentTypeException
{
    CannotInheritFromFinalContentTypeException( final ContentType contentType )
    {
        super( contentType );
    }
}
