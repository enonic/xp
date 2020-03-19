package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.security.SecurityService;

abstract class AbstractProjectRolesCommand
{
    final SecurityService securityService;

    AbstractProjectRolesCommand( final Builder builder )
    {
        this.securityService = builder.securityService;
    }

    public static class Builder<B extends Builder>
    {
        private SecurityService securityService;

        @SuppressWarnings("unchecked")
        public B securityService( final SecurityService securityService )
        {
            this.securityService = securityService;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( securityService, "securityService cannot be null" );
        }

    }

}
