package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

final class ImageMediaPathStrategyParams
{
    private final Supplier<Media> mediaSupplier;

    private final ProjectName projectName;

    private final Branch branch;

    private final String scale;

    private final String format;

    private ImageMediaPathStrategyParams( final Builder builder )
    {
        this.mediaSupplier = Objects.requireNonNull( builder.mediaSupplier );
        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.scale = Objects.requireNonNull( builder.scale );
        this.format = builder.format;
    }

    public Supplier<Media> getMedia()
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

        private ProjectName projectName;

        private Branch branch;

        private String scale;

        private String format;

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
