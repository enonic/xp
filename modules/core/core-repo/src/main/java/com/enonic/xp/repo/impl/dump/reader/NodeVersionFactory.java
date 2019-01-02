package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;

class NodeVersionFactory
{
    private final NodeVersionJsonSerializer serializer = NodeVersionJsonSerializer.create( false );

    NodeVersion create( final ByteSource dataByteSource, final ByteSource indexConfigByteSource )
    {
        final CharSource dataCharSource = dataByteSource.asCharSource( Charsets.UTF_8 );
        final CharSource indexConfigCharSource = indexConfigByteSource.asCharSource( Charsets.UTF_8 );

        try
        {
            return serializer.toNodeVersion( dataCharSource.read(), indexConfigCharSource.read() );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read node version", e );
        }
    }


}
