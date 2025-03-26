package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.project.ProjectName;

final class AttachmentMediaPathStrategyParams
{
    private final Supplier<Content> contentSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final String name;

    private final String label;

    private AttachmentMediaPathStrategyParams( final Builder builder )
    {
        this.contentSupplier = builder.contentSupplier;
        this.projectNameSupplier = builder.projectNameSupplier;
        this.branchSupplier = builder.branchSupplier;
        this.name = builder.name;
        this.label = builder.label;
    }

    public Supplier<Content> getContentSupplier()
    {
        return contentSupplier;
    }

    public Supplier<ProjectName> getProjectName()
    {
        return projectNameSupplier;
    }

    public Supplier<Branch> getBranch()
    {
        return branchSupplier;
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

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private String name;

        private String label;

        public Builder setContent( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
            return this;
        }

        public Builder setProjectName( final Supplier<ProjectName> projectNameSupplier )
        {
            this.projectNameSupplier = projectNameSupplier;
            return this;
        }

        public Builder setBranch( final Supplier<Branch> branchSupplier )
        {
            this.branchSupplier = branchSupplier;
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
