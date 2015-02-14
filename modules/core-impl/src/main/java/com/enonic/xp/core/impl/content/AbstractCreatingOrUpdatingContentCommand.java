package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.security.User;

class AbstractCreatingOrUpdatingContentCommand
    extends AbstractContentCommand
{
    final MixinService mixinService;

    final ModuleService moduleService;

    AbstractCreatingOrUpdatingContentCommand( final Builder builder )
    {
        super( builder );
        this.mixinService = builder.mixinService;
        this.moduleService = builder.moduleService;
    }

    public static class Builder<B extends Builder>
        extends AbstractContentCommand.Builder<B>
    {
        private MixinService mixinService;

        private ModuleService moduleService;

        Builder()
        {
        }

        Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
            this.mixinService = source.mixinService;
            this.moduleService = source.moduleService;
        }

        @SuppressWarnings("unchecked")
        B mixinService( final MixinService mixinService )
        {
            this.mixinService = mixinService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B moduleService( final ModuleService moduleService )
        {
            this.moduleService = moduleService;
            return (B) this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( moduleService );
            Preconditions.checkNotNull( mixinService );
        }
    }

    User getCurrentUser()
    {
        final Context context = ContextAccessor.current();

        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
    }
}


