package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;

public class BuiltinContentTypesAccessor
{
    private static final BuiltinContentTypes BUILTIN_CONTENT_TYPES = new BuiltinContentTypes();

    private BuiltinContentTypesAccessor()
    {
    }

    public static ContentTypes getAll()
    {
        return BUILTIN_CONTENT_TYPES.getAll();
    }

    public static ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return BUILTIN_CONTENT_TYPES.getContentType( contentTypeName );
    }
}
