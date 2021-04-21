package com.enonic.xp.core.impl.schema.content;

import java.util.Collection;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

public class BuiltinContentTypesAccessor
{
    private static final BuiltinContentTypes BUILTIN_CONTENT_TYPES = new BuiltinContentTypes();

    private BuiltinContentTypesAccessor()
    {
    }

    public static Collection<ContentType> getAll()
    {
        return BUILTIN_CONTENT_TYPES.getAll();
    }

    public static ContentType getContentType( final ContentTypeName contentTypeName )
    {
        return BUILTIN_CONTENT_TYPES.getContentType( contentTypeName );
    }
}
