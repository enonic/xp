package com.enonic.xp.repo.impl.node.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.util.Exceptions;

public final class NodeVersionJsonSerializer
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    public byte[] toNodeString( final NodeVersion nodeVersion )
    {
        try
        {
            return MAPPER.writeValueAsBytes( NodeVersionDataJson.toJson( nodeVersion ) );
        }
        catch ( final JsonProcessingException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public byte[] toIndexConfigDocumentString( final NodeVersion nodeVersion )
    {
        try
        {
            final IndexConfigDocumentJson entityIndexConfig = createEntityIndexConfig( nodeVersion.getIndexConfigDocument() );
            return MAPPER.writeValueAsBytes( entityIndexConfig );

        }
        catch ( final JsonProcessingException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public byte[] toAccessControlString( final NodeVersion nodeVersion )
    {
        try
        {
            final AccessControlJson accessControlJson = AccessControlJson.toJson( nodeVersion );
            return MAPPER.writeValueAsBytes( accessControlJson );

        }
        catch ( final JsonProcessingException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public NodeVersion toNodeVersion( final byte[] data, final byte[] indexConfigDocument, byte[] accessControl )
    {
        try
        {
            final NodeVersionDataJson nodeVersionJson = MAPPER.readValue( data, NodeVersionDataJson.class );
            final IndexConfigDocumentJson indexConfigDocumentJson = MAPPER.readValue( indexConfigDocument, IndexConfigDocumentJson.class );
            final AccessControlJson accessControlJson = MAPPER.readValue( accessControl, AccessControlJson.class );

            return nodeVersionJson.fromJson().
                indexConfigDocument( indexConfigDocumentJson.fromJson() ).
                inheritPermissions( accessControlJson.isInheritPermissions() ).
                permissions( accessControlJson.getAccessControlList() ).
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

    public static NodeVersionJsonSerializer create()
    {
        return new NodeVersionJsonSerializer();
    }
}
