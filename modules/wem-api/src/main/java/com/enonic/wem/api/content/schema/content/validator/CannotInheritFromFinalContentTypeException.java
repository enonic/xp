package com.enonic.wem.api.content.schema.content.validator;

import com.enonic.wem.api.content.schema.content.ContentType;

public final class CannotInheritFromFinalContentTypeException
    extends InvalidContentTypeException
{
    CannotInheritFromFinalContentTypeException( final ContentType contentType )
    {
        super( contentType );
    }
}
