package com.enonic.wem.api.content.schema.content.editor;

import com.enonic.wem.api.content.schema.content.ContentType;

public interface ContentTypeEditor
{
    /**
     * @param contentType to be edited
     * @return updated content type, null if it has not been updated.
     */
    public ContentType edit( ContentType contentType )
        throws Exception;
}
