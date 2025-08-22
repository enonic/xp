package com.enonic.xp.lib.project.command;

import java.util.Objects;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.project.ProjectName;

abstract class AbstractProjectRootCommand
{
    final Context projectRepoContext;

    final ContentService contentService;

    final ProjectName projectName;

    AbstractProjectRootCommand( final Builder builder )
    {
        this.contentService = builder.contentService;
        this.projectName = builder.projectName;

        this.projectRepoContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( projectName.getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            build();
    }

    public static class Builder<BUILDER extends Builder>
    {
        private ContentService contentService;

        private ProjectName projectName;

        @SuppressWarnings("unchecked")
        public BUILDER contentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return (BUILDER) this;
        }

        @SuppressWarnings("unchecked")
        public BUILDER projectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return (BUILDER) this;
        }

        void validate()
        {
            Objects.requireNonNull( contentService );
            Objects.requireNonNull( projectName, "projectName is required" );
        }
    }

}
