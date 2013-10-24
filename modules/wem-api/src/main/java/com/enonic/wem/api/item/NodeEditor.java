package com.enonic.wem.api.item;

public interface NodeEditor
{
    /**
     * @param toBeEdited to be edited
     * @return updated item, null if it has no change was necessary.
     */
    public Node edit( Node toBeEdited );
}
