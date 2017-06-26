package com.enonic.xp.dump;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class SystemLoadResult
    implements Iterable<RepoLoadResult>
{
    private final List<RepoLoadResult> repoLoadResults;

    private SystemLoadResult( final Builder builder )
    {
        repoLoadResults = builder.repoLoadResults;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<RepoLoadResult> iterator()
    {
        return this.repoLoadResults.iterator();
    }

    public static final class Builder
    {
        private final List<RepoLoadResult> repoLoadResults = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder add( final RepoLoadResult val )
        {
            repoLoadResults.add( val );
            return this;
        }

        public SystemLoadResult build()
        {
            return new SystemLoadResult( this );
        }
    }
}
