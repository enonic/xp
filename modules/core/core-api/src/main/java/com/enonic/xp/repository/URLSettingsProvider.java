package com.enonic.xp.repository;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.json.ObjectMapperHelper;

public class URLSettingsProvider
    implements SettingsProvider
{
    private final static ObjectMapper mapper = ObjectMapperHelper.create();

    private final JsonNode settings;

    private URLSettingsProvider( final JsonNode settings )
    {
        this.settings = settings;
    }

    public static SettingsProvider from( final URL url )
    {
        try
        {
            return new URLSettingsProvider( mapper.readTree( Resources.toString( url, Charsets.UTF_8 ) ) );
        }
        catch ( IOException | IllegalArgumentException e )
        {
            throw new RepositoryExeption( "Failed to load settings from URL: " + url, e );
        }
    }

    @Override
    public JsonNode get()
    {
        return this.settings;
    }
}
