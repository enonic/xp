package com.enonic.xp.lib.project.command;

import java.util.Locale;
import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.User;

public final class ApplyProjectLanguageCommand
    extends AbstractProjectRootCommand
{
    private final Locale language;

    private ApplyProjectLanguageCommand( final Builder builder )
    {
        super( builder );
        this.language = builder.language;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Locale execute()
    {
        return doUpdateLanguage();
    }

    private Locale doUpdateLanguage()
    {
        return projectRepoContext.callWith( () -> {
            final Content root = this.contentService.getByPath( ContentPath.ROOT );

            final UpdateContentParams params = new UpdateContentParams().
                contentId( root.getId() ).
                editor( edit -> edit.language = this.language );

            return this.contentService.update( params ).
                getLanguage();
        } );
    }

    private User getCurrentUser()
    {
        return Objects.requireNonNullElse( ContextAccessor.current().getAuthInfo().getUser(), User.ANONYMOUS );
    }

    public static final class Builder
        extends AbstractProjectRootCommand.Builder<Builder>
    {
        private Locale language;

        private Builder()
        {
        }

        public Builder language( final Locale language )
        {
            this.language = language;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
        }

        public ApplyProjectLanguageCommand build()
        {
            validate();
            return new ApplyProjectLanguageCommand( this );
        }
    }
}
