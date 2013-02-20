package com.enonic.wem.api.content.schema.type.validator;

import com.enonic.wem.api.content.schema.type.ContentType;

public final class CannotInheritFromFinalContentTypeException
    extends InvalidContentTypeException
{
    CannotInheritFromFinalContentTypeException( final ContentType contentType )
    {
        super( contentType );
    }
}
