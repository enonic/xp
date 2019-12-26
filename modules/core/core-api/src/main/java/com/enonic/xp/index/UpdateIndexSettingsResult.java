package com.enonic.xp.index;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
        private final Set<String> updatedIndexes = new HashSet<>();

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
