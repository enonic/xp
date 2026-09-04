package com.enonic.xp.index;

import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;


public final class UpdateIndexSettingsParams
{
    private final RepositoryIds repositoryIds;

    private final String settings;

    private final boolean requireClosedIndex;

    private final IndexType indexType;

    private UpdateIndexSettingsParams( Builder builder )
    {
        repositoryIds = RepositoryIds.from( builder.repositoryIds );
        settings = builder.settings;
        this.requireClosedIndex = builder.requireClosedIndex;
        this.indexType = builder.indexType;
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

    /**
     * Returns the index the settings apply to: {@link IndexType#SEARCH} for the search index, any other type for the
     * storage index (version, branch and commit data share one index). {@code null} applies the settings to both.
     */
    public IndexType getIndexType()
    {
        return indexType;
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

        private IndexType indexType;

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

        public Builder indexType( final IndexType indexType )
        {
            this.indexType = indexType;
            return this;
        }

        public UpdateIndexSettingsParams build()
        {
            return new UpdateIndexSettingsParams( this );
        }
    }
}


