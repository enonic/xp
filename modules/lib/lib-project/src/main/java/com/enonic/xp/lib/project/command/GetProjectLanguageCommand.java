package com.enonic.xp.lib.project.command;

import java.util.Locale;
import java.util.Optional;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;

public final class GetProjectLanguageCommand
    extends AbstractProjectRootCommand
{
    private GetProjectLanguageCommand( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Locale execute()
    {
        return doGetLanguage();
    }

    private Locale doGetLanguage()
    {
        return Optional.ofNullable( projectRepoContext.callWith( () -> this.contentService.getByPath( ContentPath.ROOT ) ) ).
            map( Content::getLanguage ).
            orElse( null );
    }

    public static final class Builder
        extends AbstractProjectRootCommand.Builder<Builder>
    {
        private Builder()
        {
        }

        void validate()
        {
            super.validate();
        }

        public GetProjectLanguageCommand build()
        {
            validate();
            return new GetProjectLanguageCommand( this );
        }
    }
}
