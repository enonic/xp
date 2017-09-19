package com.enonic.xp.repo.impl.vacuum.version;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.node.NodeId;

@Component(immediate = true)
public class NodeIdResolverImpl
    implements NodeIdResolver
{
    private final ObjectMapper mapper = new ObjectMapper();

    private final static Logger LOG = LoggerFactory.getLogger( NodeIdResolverImpl.class );

    @Override
    public NodeId resolve( final BlobRecord record )
    {
        try (final InputStream stream = record.getBytes().openStream())
        {
            final JsonNode node = this.mapper.readTree( stream );

            final JsonNode idNode = node.get( "id" );

            if ( idNode == null )
            {
                LOG.error( "Not able to get node-id from file " + record.getKey() );
                return null;
            }

            return NodeId.from( idNode.asText() );
        }
        catch ( IOException e )
        {
            return null;
        }
    }
}
