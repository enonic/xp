package com.enonic.wem.security;

import com.google.common.collect.ImmutableSet;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Subject
{
    private User user;

    private PrincipalKeys principals;

    private Subject( final Builder builder )
    {
        this.user = checkNotNull( builder.user, "Subject user cannot be null" );
        builder.principals.add( user.getKey() );
        this.principals = PrincipalKeys.from( builder.principals.build() );
    }

    public User getUser()
    {
        return user;
    }

    public PrincipalKeys getPrincipals()
    {
        return principals;
    }

    public boolean hasRole( final String role )
    {
        return principals.stream().anyMatch( principal -> principal.isRole() && principal.getId().equals( role ) );
    }

    public static Builder newSubject()
    {
        return new Builder();
    }

    public static Builder newSubject( final Subject subject )
    {
        return new Builder( subject );
    }

    public static class Builder
    {
        private User user;

        private final ImmutableSet.Builder<PrincipalKey> principals;

        private Builder()
        {
            principals = ImmutableSet.builder();
        }

        private Builder( final Subject subject )
        {
            this.user = subject.user;
            this.principals = ImmutableSet.builder();
            this.principals.addAll( subject.principals.getSet() );
        }

        public Builder user( final User user )
        {
            this.user = user;
            return this;
        }

        public Builder principal( final PrincipalKey principal )
        {
            this.principals.add( principal );
            return this;
        }

        public Builder principals( final Iterable<PrincipalKey> principals )
        {
            this.principals.addAll( principals );
            return this;
        }

        public Subject build()
        {
            return new Subject( this );
        }
    }

}
