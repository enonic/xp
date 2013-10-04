package com.enonic.wem.api.item;

import org.joda.time.DateTime;

public interface ItemEditor
{
    public DateTime getReadAt();

    /**
     * @param toBeEdited to be edited
     * @return updated item, null if it has no change was necessary.
     */
    public Item edit( Item toBeEdited );
}
