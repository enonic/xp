package com.enonic.xp.portal.url;

public final class ImageMediaUrlParams
    extends UrlParams
{
    private final String projectName;

    private final String branch;

    private final String siteKey;

    private final String background;

    private final Integer quality;

    private final String filter;

    private final String format;

    private final String scale;

    private ImageMediaUrlParams( final Builder builder )
    {
        super( builder );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.siteKey = builder.siteKey;
        this.background = builder.background;
        this.quality = builder.quality;
        this.filter = builder.filter;
        this.format = builder.format;
        this.scale = builder.scale;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getBranch()
    {
        return branch;
    }

    public String getSiteKey()
    {
        return siteKey;
    }

    public String getBackground()
    {
        return background;
    }

    public Integer getQuality()
    {
        return quality;
    }

    public String getFilter()
    {
        return filter;
    }

    public String getFormat()
    {
        return format;
    }

    public String getScale()
    {
        return scale;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends UrlParams.Builder<Builder>
    {
        private String projectName;

        private String branch;

        private String siteKey;

        private String background;

        private Integer quality;

        private String filter;

        private String format;

        private String scale;

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

        public Builder siteKey( final String siteKey )
        {
            this.siteKey = siteKey;
            return this;
        }


        public Builder background( final String background )
        {
            this.background = background;
            return this;
        }

        public Builder quality( final Integer quality )
        {
            this.quality = quality;
            return this;
        }

        public Builder filter( final String filter )
        {
            this.filter = filter;
            return this;
        }

        public Builder format( final String format )
        {
            this.format = format;
            return this;
        }

        public Builder scale( final String scale )
        {
            this.scale = scale;
            return this;
        }

        public ImageMediaUrlParams build()
        {
            return new ImageMediaUrlParams( this );
        }
    }

}
