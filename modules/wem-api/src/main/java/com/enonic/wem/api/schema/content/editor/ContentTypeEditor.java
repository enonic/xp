package com.enonic.wem.api.schema.content.editor;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.support.Editor;

public interface ContentTypeEditor
    extends Editor<ContentType>
{
    /**
     * @param contentType to be edited
     * @return updated content type, null if it has not been updated.
     */
    public ContentType edit( ContentType contentType );
}
