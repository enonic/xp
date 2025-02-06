package com.enonic.xp.portal.url;

import java.util.Objects;

public final class AttachmentMediaUrlParams
    extends UrlParams
{
    private final String projectName;

    private final String branch;

    private final String name;

    private final String label;

    private final Boolean download;

    private AttachmentMediaUrlParams( final Builder builder )
    {
        super( builder );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.name = builder.name;
        this.label = builder.label;
        this.download = Objects.requireNonNullElse( builder.download, false );
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getBranch()
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

    public boolean isDownload()
    {
        return download;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends UrlParams.Builder<Builder>
    {
        private String projectName;

        private String branch;

        private String name;

        private String label;

        private Boolean download;

        public Builder projectName( final String projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder branch( final String branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder label( final String value )
        {
            this.label = value;
            return this;
        }

        public Builder download( final Boolean value )
        {
            this.download = value;
            return this;
        }

        public AttachmentMediaUrlParams build()
        {
            return new AttachmentMediaUrlParams( this );
        }
    }
}
