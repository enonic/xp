package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

final class ImageMediaPathStrategyParams
{
    private final Media media;

    private final ProjectName projectName;

    private final Branch branch;

    private final String scale;

    private final String format;

    public ImageMediaPathStrategyParams( final Builder builder )
    {
        this.media = Objects.requireNonNull( builder.media );
        this.projectName = Objects.requireNonNull( builder.projectName );
        this.branch = Objects.requireNonNull( builder.branch );
        this.scale = Objects.requireNonNull( builder.scale );
        this.format = builder.format;
    }

    public Media getMedia()
    {
        return media;
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

    public static class Builder
    {
        private Media media;

        private ProjectName projectName;

        private Branch branch;

        private String scale;

        private String format;

        public Builder setMedia( final Media media )
        {
            this.media = media;
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
