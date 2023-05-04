package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.app.ApplicationKey;

public final class WebappJson
{
    private final String name;

    private WebappJson( Builder builder )
    {
        this.name = builder.name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static WebappJson from( final ApplicationKey key )
    {
        return create().name( key.toString() ).build();
    }

    public String getName()
    {
        return name;
    }

    public static class Builder
    {
        private String name;

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public WebappJson build()
        {
            return new WebappJson( this );
        }
    }
}
