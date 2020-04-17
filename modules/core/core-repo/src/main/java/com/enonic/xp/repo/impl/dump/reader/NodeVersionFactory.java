package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;

import com.google.common.io.ByteSource;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;

class NodeVersionFactory
{
    private final NodeVersionJsonSerializer serializer = NodeVersionJsonSerializer.create();

    NodeVersion create( final ByteSource dataByteSource, final ByteSource indexConfigByteSource, final ByteSource accessControlByteSource )
    {
        try
        {
            return serializer.toNodeVersion( dataByteSource.read(), indexConfigByteSource.read(), accessControlByteSource.read() );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read node version", e );
        }
    }
}
