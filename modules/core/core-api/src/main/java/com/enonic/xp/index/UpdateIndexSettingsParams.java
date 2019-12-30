package com.enonic.xp.index;

import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

@PublicApi
public class UpdateIndexSettingsParams
{
    private final RepositoryIds repositoryIds;

    private final String settings;

    private final boolean requireClosedIndex;

    private UpdateIndexSettingsParams( Builder builder )
    {
        repositoryIds = RepositoryIds.from( builder.repositoryIds );
        settings = builder.settings;
        this.requireClosedIndex = builder.requireClosedIndex;
    }

    public RepositoryIds getRepositoryIds()
    {
        return repositoryIds;
    }

    public String getSettings()
    {
        return settings;
    }

    public boolean isRequireClosedIndex()
    {
        return requireClosedIndex;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Set<RepositoryId> repositoryIds = new HashSet<>();

        private String settings;

        private boolean requireClosedIndex = false;

        private Builder()
        {
        }

        public Builder repository( final RepositoryId repositorId )
        {
            this.repositoryIds.add( repositorId );
            return this;
        }

        public Builder repositories( final RepositoryIds repositoryIds )
        {
            this.repositoryIds = repositoryIds.getSet();
            return this;
        }

        public Builder settings( final String settings )
        {
            this.settings = settings;
            return this;
        }

        public Builder requireClosedIndex( final boolean requireClosedIndex )
        {
            this.requireClosedIndex = requireClosedIndex;
            return this;
        }

        public UpdateIndexSettingsParams build()
        {
            return new UpdateIndexSettingsParams( this );
        }
    }
}


