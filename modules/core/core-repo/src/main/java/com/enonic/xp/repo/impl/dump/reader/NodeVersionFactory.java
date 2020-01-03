package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;

class NodeVersionFactory
{
    private final NodeVersionJsonSerializer serializer = NodeVersionJsonSerializer.create();

    NodeVersion create( final ByteSource dataByteSource, final ByteSource indexConfigByteSource, final ByteSource accessControlByteSource )
    {
        final CharSource dataCharSource = dataByteSource.asCharSource( StandardCharsets.UTF_8 );
        final CharSource indexConfigCharSource = indexConfigByteSource.asCharSource( StandardCharsets.UTF_8 );
        final CharSource accessControlCharSource = accessControlByteSource.asCharSource( StandardCharsets.UTF_8 );

        try
        {
            return serializer.toNodeVersion( dataCharSource.read(), indexConfigCharSource.read(), accessControlCharSource.read() );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read node version", e );
        }
    }


}
