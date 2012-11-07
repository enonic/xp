package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.ContentType;

public interface ContentTypeEditor
{
    public boolean edit( ContentType contentType )
        throws Exception;
}
