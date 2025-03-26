package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

final class ImageMediaPathStrategyParams
{
    private final Supplier<Media> mediaSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final String scale;

    private final String format;

    private ImageMediaPathStrategyParams( final Builder builder )
    {
        this.mediaSupplier = Objects.requireNonNull( builder.mediaSupplier );
        this.projectNameSupplier = Objects.requireNonNull( builder.projectNameSupplier );
        this.branchSupplier = Objects.requireNonNull( builder.branchSupplier );
        this.scale = Objects.requireNonNull( builder.scale );
        this.format = builder.format;
    }

    public Supplier<Media> getMedia()
    {
        return mediaSupplier;
    }

    public Supplier<ProjectName> getProjectName()
    {
        return projectNameSupplier;
    }

    public Supplier<Branch> getBranch()
    {
        return branchSupplier;
    }

    public String getScale()
    {
        return scale;
    }

    public String getFormat()
    {
        return format;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private Supplier<Media> mediaSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private String scale;

        private String format;

        public Builder setMedia( final Supplier<Media> mediaSupplier )
        {
            this.mediaSupplier = mediaSupplier;
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

        public Builder setScale( final String scale )
        {
            this.scale = scale;
            return this;
        }

        public Builder setFormat( final String format )
        {
            this.format = format;
            return this;
        }

        public ImageMediaPathStrategyParams build()
        {
            return new ImageMediaPathStrategyParams( this );
        }
    }
}
