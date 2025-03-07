package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.project.ProjectName;

final class AttachmentMediaPathStrategyParams
{
    private final Supplier<Content> contentSupplier;

    private final ProjectName projectName;

    private final Branch branch;

    private final String name;

    private final String label;

    private AttachmentMediaPathStrategyParams( final Builder builder )
    {
        this.contentSupplier = builder.contentSupplier;
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.name = builder.name;
        this.label = builder.label;
    }

    public Supplier<Content> getContentSupplier()
    {
        return contentSupplier;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private Supplier<Content> contentSupplier;

        private ProjectName projectName;

        private Branch branch;

        private String name;

        private String label;

        public Builder setContent( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
            return this;
        }

        public Builder setProjectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder setBranch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder setName( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder setLabel( final String label )
        {
            this.label = label;
            return this;
        }

        public AttachmentMediaPathStrategyParams build()
        {
            return new AttachmentMediaPathStrategyParams( this );
        }
    }
}
