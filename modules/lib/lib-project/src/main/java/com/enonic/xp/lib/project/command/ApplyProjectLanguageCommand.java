package com.enonic.xp.lib.project.command;

import java.util.Locale;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateContentMetadataParams;

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

            final UpdateContentMetadataParams params =
                UpdateContentMetadataParams.create().contentId( root.getId() ).editor( edit -> edit.language = this.language ).build();

            return this.contentService.updateMetadata( params ).getContent().getLanguage();
        } );
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
