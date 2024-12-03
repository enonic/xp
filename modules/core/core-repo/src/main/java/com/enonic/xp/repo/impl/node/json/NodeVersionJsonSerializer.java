package com.enonic.xp.repo.impl.node.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;

import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.security.acl.AccessControlList;

public final class NodeVersionJsonSerializer
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    public static byte[] toNodeVersionBytes( final NodeVersion nodeVersion )
    {
        return writeValueAsBytes( NodeVersionDataJson.toJson( nodeVersion ) );
    }

    public static byte[] toIndexConfigDocumentBytes( final NodeVersion nodeVersion )
    {
        final IndexConfigDocumentJson entityIndexConfig;
        final IndexConfigDocument indexConfig = nodeVersion.getIndexConfigDocument();
        if ( indexConfig instanceof PatternIndexConfigDocument )
        {
            entityIndexConfig = IndexConfigDocumentJson.toJson( (PatternIndexConfigDocument) indexConfig );
        }
        else
        {
            entityIndexConfig = null;
        }
        return writeValueAsBytes( entityIndexConfig );
    }

    public static byte[] toAccessControlBytes( final NodeVersion nodeVersion )
    {
        return writeValueAsBytes( AccessControlJson.toJson( nodeVersion ) );
    }

    public static NodeVersion toNodeVersion( final ByteSource data, final ByteSource indexConfigDocumentData, ByteSource accessControlData )
        throws IOException
    {
        final NodeVersion nodeVersion = toNodeVersionData( data );

        final PatternIndexConfigDocument indexConfigDocument = toIndexConfigDocument( indexConfigDocumentData );

        final AccessControlList accessControl = toNodeVersionAccessControl( accessControlData );

        return NodeVersion.create( nodeVersion )
            .indexConfigDocument( indexConfigDocument )
            .permissions( accessControl )
            .build();
    }

    public static NodeVersion toNodeVersionData( final ByteSource data )
        throws IOException
    {
        return NodeVersionDataJson.fromJson( readValue( data, NodeVersionDataJson.class ) );
    }

    public static PatternIndexConfigDocument toIndexConfigDocument( final ByteSource data )
        throws IOException
    {
        return IndexConfigDocumentJson.fromJson( readValue( data, IndexConfigDocumentJson.class ) );
    }

    public static AccessControlList toNodeVersionAccessControl( final ByteSource data )
        throws IOException
    {
        return AccessControlJson.fromJson( readValue( data, AccessControlJson.class ) );
    }

    private static <T> T readValue( final ByteSource src, final Class<T> valueType )
        throws IOException
    {
        try (InputStream stream = src.openBufferedStream())
        {
            return MAPPER.readValue( stream, valueType );
        }
    }

    private static byte[] writeValueAsBytes( Object object )
    {
        try
        {
            return MAPPER.writeValueAsBytes( object );
        }
        catch ( JsonProcessingException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
