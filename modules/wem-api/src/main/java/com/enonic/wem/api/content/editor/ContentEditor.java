package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;

public interface ContentEditor
{
    /**
     * @param content to be edited
     * @return updated content, null if it has not been updated.
     */
    public Content edit( Content content )
        throws Exception;
}
