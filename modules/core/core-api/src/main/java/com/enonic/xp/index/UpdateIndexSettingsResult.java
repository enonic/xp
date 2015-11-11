package com.enonic.xp.index;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Beta
public class UpdateIndexSettingsResult
{
    private final ImmutableSet<String> updatedIndexes;

    private UpdateIndexSettingsResult( Builder builder )
    {
        this.updatedIndexes = ImmutableSet.copyOf( builder.updatedIndexes );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<String> getUpdatedIndexes()
    {
        return updatedIndexes;
    }

    public static final class Builder
    {
        private final Set<String> updatedIndexes = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder addUpdatedIndex( final String updatedIndex )
        {
            this.updatedIndexes.add( updatedIndex );
            return this;
        }

        public UpdateIndexSettingsResult build()
        {
            return new UpdateIndexSettingsResult( this );
        }
    }
}
