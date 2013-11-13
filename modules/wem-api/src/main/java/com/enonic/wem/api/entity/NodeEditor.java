package com.enonic.wem.api.entity;

import com.enonic.wem.api.support.Editor;

public interface NodeEditor
    extends Editor<Node>
{
    /**
     * @param toBeEdited to be edited
     * @return updated item, null if it has no change was necessary.
     */
    public Node edit( Node toBeEdited );
}
