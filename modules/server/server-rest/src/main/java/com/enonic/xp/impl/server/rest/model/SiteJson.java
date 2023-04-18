package com.enonic.xp.impl.server.rest.model;

public final class SiteJson
{
    private final String displayName;

    private final String path;

    private final String language;

    private SiteJson( Builder builder )
    {
        this.displayName = builder.displayName;
        this.path = builder.path;
        this.language = builder.language;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getPath()
    {
        return path;
    }

    public String getLanguage()
    {
        return language;
    }

    public static class Builder
    {
        String displayName;

        String path;

        String language;

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder path( final String path )
        {
            this.path = path;
            return this;
        }

        public Builder language( final String language )
        {
            this.language = language;
            return this;
        }

        public SiteJson build()
        {
            return new SiteJson( this );
        }
    }
}
