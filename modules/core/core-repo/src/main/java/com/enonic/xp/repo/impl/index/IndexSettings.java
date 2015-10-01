package com.enonic.xp.repo.impl.index;

import com.fasterxml.jackson.databind.JsonNode;

public class IndexSettings
{
    private final String settings;

    private IndexSettings( final String settings )
    {
        this.settings = settings;
    }

    public static IndexSettings from( final String settings )
    {
        return new IndexSettings( settings );
    }

    public static IndexSettings from( final JsonNode jsonNode )
    {
        return new IndexSettings( jsonNode.toString() );
    }

    public String getSettingsAsString()
    {
        return settings;
    }
}
