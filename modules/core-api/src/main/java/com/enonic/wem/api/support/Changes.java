package com.enonic.wem.api.support;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Changes
    implements Iterable<Change>
{
    private final ImmutableList<Change> changes;

    public Changes( final Builder builder )
    {
        changes = ImmutableList.copyOf( builder.changes );
    }

    public boolean isChanges()
    {
        return !this.changes.isEmpty();
    }

    public boolean isNoChanges()
    {
        return this.changes.isEmpty();
    }

    @Override
    public Iterator<Change> iterator()
    {
        return changes.iterator();
    }

    public static class Builder
    {
        private List<Change> changes = new ArrayList<>();

        public Builder recordChange( final PossibleChange change )
        {
            change.addChange( this );
            return this;
        }

        public Builder recordChange( final Change change )
        {
            changes.add( change );
            return this;
        }

        public boolean isChanges()
        {
            return !this.changes.isEmpty();
        }

        public boolean isNoChanges()
        {
            return this.changes.isEmpty();
        }

        public Changes build()
        {
            return new Changes( this );
        }
    }
}
