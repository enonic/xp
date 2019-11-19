package com.enonic.xp.impl.server.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;

public final class ReprocessContentResultJson
{
    public final List<String> updatedContent;

    public final List<String> errors;

    public ReprocessContentResultJson( final ContentPaths result, final List<String> errors )
    {
        this.updatedContent = result.stream().map( ContentPath::toString ).collect( Collectors.toList() );
        this.errors = errors;
    }

    @Override
    public String toString()
    {
        final ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writeValueAsString( this );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }
}
