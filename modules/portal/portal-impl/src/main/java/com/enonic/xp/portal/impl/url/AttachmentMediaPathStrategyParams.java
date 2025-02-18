package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

final class AttachmentMediaPathStrategyParams
{
    private final Supplier<Media> mediaSupplier;

    private final ProjectName projectName;

    private final Branch branch;

    private final boolean download;

    private final String name;

    private final String label;

    private AttachmentMediaPathStrategyParams( final Builder builder )
    {
        this.mediaSupplier = builder.mediaSupplier;
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.download = builder.download;
        this.name = builder.name;
        this.label = builder.label;
    }

    public Supplier<Media> getMediaSupplier()
    {
        return mediaSupplier;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public boolean isDownload()
    {
        return download;
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
        private Supplier<Media> mediaSupplier;

        private ProjectName projectName;

        private Branch branch;

        private boolean download;

        private String name;

        private String label;

        public Builder setMedia( final Supplier<Media> mediaSupplier )
        {
            this.mediaSupplier = mediaSupplier;
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

        public Builder setDownload( final boolean download )
        {
            this.download = download;
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
