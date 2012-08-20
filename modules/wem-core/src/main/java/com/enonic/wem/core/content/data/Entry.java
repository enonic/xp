package com.enonic.wem.core.content.data;

/**
 * Entry is the base class for Value and Entries.
 */
public abstract class Entry
{
    public abstract EntryPath getPath();

    public String getName()
    {
        return getPath().getLastElement().getName();
    }

    public abstract boolean breaksRequiredContract();

    public abstract void checkBreaksRequiredContract();
}
