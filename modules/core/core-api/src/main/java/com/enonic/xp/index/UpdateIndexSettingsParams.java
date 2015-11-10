package com.enonic.xp.index;

import com.google.common.annotations.Beta;

@Beta
public class UpdateIndexSettingsParams
{
    private final String indexName;

    private final String settings;

    private UpdateIndexSettingsParams( Builder builder )
    {
        indexName = builder.indexName;
        settings = builder.settings;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public String getSettings()
    {
        return settings;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String indexName;

        private String settings;

        private Builder()
        {
        }

        public Builder indexName( final String indexName )
        {
            this.indexName = indexName;
            return this;
        }

        public Builder settings( final String settings )
        {
            this.settings = settings;
            return this;
        }

        public UpdateIndexSettingsParams build()
        {
            return new UpdateIndexSettingsParams( this );
        }
    }
}


