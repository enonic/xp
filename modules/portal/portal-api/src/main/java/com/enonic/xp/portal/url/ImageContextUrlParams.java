package com.enonic.xp.portal.url;

import java.util.Objects;

public class ImageContextUrlParams
{
    private final UrlContext urlContext;

    private final String id;

    private final String path;

    private final String background;

    private final Integer quality;

    private final String filter;

    private final String format;

    private final String scale;

    private ImageContextUrlParams( final Builder builder )
    {
        this.urlContext = Objects.requireNonNull( builder.urlContext );
        this.id = builder.id;
        this.path = builder.path;
        this.background = builder.background;
        this.quality = builder.quality;
        this.filter = builder.filter;
        this.format = builder.format;
        this.scale = builder.scale;
    }

    public UrlContext getUrlContext()
    {
        return urlContext;
    }

    public String getId()
    {
        return id;
    }

    public String getPath()
    {
        return path;
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

    public static class Builder
    {
        private UrlContext urlContext;

        private String id;

        private String path;

        private String background;

        private Integer quality;

        private String filter;

        private String format;

        private String scale;

        public Builder setUrlContext( final UrlContext urlContext )
        {
            this.urlContext = urlContext;
            return this;
        }

        public Builder setId( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder setPath( final String path )
        {
            this.path = path;
            return this;
        }

        public Builder setBackground( final String background )
        {
            this.background = background;
            return this;
        }

        public Builder setQuality( final Integer quality )
        {
            this.quality = quality;
            return this;
        }

        public Builder setFilter( final String filter )
        {
            this.filter = filter;
            return this;
        }

        public Builder setFormat( final String format )
        {
            this.format = format;
            return this;
        }

        public Builder setScale( final String scale )
        {
            this.scale = scale;
            return this;
        }

        public ImageContextUrlParams build()
        {
            return new ImageContextUrlParams( this );
        }
    }

}
