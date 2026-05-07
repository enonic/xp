package com.enonic.xp.lib.project.command;

import java.util.Locale;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.context.ContextAccessor;

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

            final PatchContentParams params = PatchContentParams.create()
                .contentId( root.getId() )
                .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                .patcher( edit -> edit.language.setValue( this.language ) )
                .build();

            final PatchContentResult result = this.contentService.patch( params );

            return result.getResult( ContextAccessor.current().getBranch() ).getLanguage();
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
