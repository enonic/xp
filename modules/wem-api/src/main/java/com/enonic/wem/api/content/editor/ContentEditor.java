package com.enonic.wem.api.content.editor;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.support.Editor;

public interface ContentEditor
    extends Editor<Content>
{
    /**
     * @param toBeEdited to be edited
     * @return updated content, null if it has no change was necessary.
     */
    public Content edit( Content toBeEdited );
}
