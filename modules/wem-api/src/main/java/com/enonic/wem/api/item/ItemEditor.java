package com.enonic.wem.api.item;

import org.joda.time.DateTime;

import com.google.common.base.Optional;

public interface ItemEditor
{
    /**
     * Optionally give the time that you read the Item to edit, so that in the case it has been updated by someone else after this time,
     * an exception ItemModifiedSinceRead will be thrown when you execute command {@link com.enonic.wem.api.item.UpdateItem}.
     */
    public Optional<DateTime> getReadAt();

    /**
     * @param toBeEdited to be edited
     * @return updated item, null if it has no change was necessary.
     */
    public Item edit( Item toBeEdited );
}
