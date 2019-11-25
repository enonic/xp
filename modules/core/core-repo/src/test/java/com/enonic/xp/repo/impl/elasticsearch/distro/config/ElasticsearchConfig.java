package com.enonic.xp.repo.impl.elasticsearch.distro.config;

import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public final class ElasticsearchConfig
{

    private final ObjectMapper objectMapper = new ObjectMapper( new YAMLFactory() );

    private final Map<String, Object> settings;

    private ElasticsearchConfig( final Builder builder )
    {
        this.settings = builder.settings;
    }

    public Map<String, Object> getSettings()
    {
        return settings;
    }

    public String toYaml()
    {
        try
        {
            return objectMapper.writeValueAsString( settings );
        }
        catch ( JsonProcessingException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private Map<String, Object> settings = new HashMap<>();

        public Builder setting( final String key, final Object value )
        {
            this.settings.put( key, value );
            return this;
        }

        public ElasticsearchConfig build()
        {
            return new ElasticsearchConfig( this );
        }

    }

}
