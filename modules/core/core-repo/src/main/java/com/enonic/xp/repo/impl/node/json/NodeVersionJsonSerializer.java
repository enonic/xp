package com.enonic.xp.repo.impl.node.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.util.Exceptions;

public final class NodeVersionJsonSerializer
{
    final ObjectMapper mapper;

    private NodeVersionJsonSerializer( final ObjectMapper mapper )
    {
        this.mapper = mapper;
    }

    public String toNodeString( final NodeVersion nodeVersion )
    {
        try
        {
            return this.mapper.writeValueAsString( NodeVersionDataJson.toJson( nodeVersion ) );

        }
        catch ( final JsonProcessingException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public String toIndexConfigDocumentString( final NodeVersion nodeVersion )
    {
        try
        {
            final IndexConfigDocumentJson entityIndexConfig = createEntityIndexConfig( nodeVersion.getIndexConfigDocument() );
            return this.mapper.writeValueAsString( entityIndexConfig );

        }
        catch ( final JsonProcessingException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public NodeVersion toNodeVersion( final String data, final String indexConfigDocument )
    {
        try
        {
            final NodeVersionDataJson nodeVersionJson = this.mapper.readValue( data, NodeVersionDataJson.class );
            final IndexConfigDocumentJson indexConfigDocumentJson =
                this.mapper.readValue( indexConfigDocument, IndexConfigDocumentJson.class );

            return nodeVersionJson.fromJson().
                indexConfigDocument( indexConfigDocumentJson.fromJson() ).
                build();
        }
        catch ( final IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private static IndexConfigDocumentJson createEntityIndexConfig( final IndexConfigDocument indexConfig )
    {
        if ( indexConfig instanceof PatternIndexConfigDocument )
        {
            return IndexConfigDocumentJson.toJson( (PatternIndexConfigDocument) indexConfig );
        }
        return null;
    }

    public static NodeVersionJsonSerializer create( final boolean indent )
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        if ( indent )
        {
            mapper.enable( SerializationFeature.INDENT_OUTPUT );
        }

        return new NodeVersionJsonSerializer( mapper );
    }
}
