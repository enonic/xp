package com.enonic.wem.api.schema.content.validator;

import com.enonic.wem.api.schema.content.ContentType;

public final class CannotInheritFromFinalContentTypeException
    extends InvalidContentTypeException
{
    CannotInheritFromFinalContentTypeException( final ContentType contentType )
    {
        super( contentType );
    }
}
