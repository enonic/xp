package com.enonic.xp.security;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PrincipalQueryResult
{
    private final int totalSize;

    private final Principals principals;

    private PrincipalQueryResult( final Builder builder )
    {
        this.totalSize = builder.totalSize;
        this.principals = Principals.from( builder.principalList.build() );
    }

    public Principals getPrincipals()
    {
        return principals;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public boolean isEmpty()
    {
        return principals.isEmpty();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int totalSize;

        private final ImmutableList.Builder<Principal> principalList;

        private Builder()
        {
            this.principalList = ImmutableList.builder();
        }

        public Builder addPrincipal( final Principal principal )
        {
            this.principalList.add( principal );
            return this;
        }

        public Builder addPrincipals( final Iterable<Principal> principals )
        {
            this.principalList.addAll( principals );
            return this;
        }

        public Builder totalSize( final int totalSize )
        {
            this.totalSize = totalSize;
            return this;
        }

        public PrincipalQueryResult build()
        {
            return new PrincipalQueryResult( this );
        }
    }

}
