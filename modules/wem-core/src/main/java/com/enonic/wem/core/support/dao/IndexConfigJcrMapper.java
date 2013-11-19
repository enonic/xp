package com.enonic.wem.core.support.dao;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.core.entity.EntityIndexConfigJson;

public class IndexConfigJcrMapper
{
    private final static String INDEX_PROPERTY = "indexConfig";

    private final JsonHelper jsonHelper = new JsonHelper();

    public void toJcr( final EntityIndexConfig indexConfig, final Node targetNode )
        throws RepositoryException
    {
        if ( indexConfig != null )
        {
            EntityIndexConfigJson indexConfigJsonObject = new EntityIndexConfigJson( indexConfig );
            targetNode.setProperty( INDEX_PROPERTY, jsonHelper.objectToString( indexConfigJsonObject ) );
        }
    }

    public EntityIndexConfig toEntityIndexConfig( final Node sourceNode )
        throws RepositoryException
    {
        if ( !sourceNode.hasProperty( INDEX_PROPERTY ) )
        {
            return null;
        }

        try
        {
            EntityIndexConfigJson indexConfigJson =
                jsonHelper.objectMapper().readValue( sourceNode.getProperty( INDEX_PROPERTY ).getString(), EntityIndexConfigJson.class );

            return indexConfigJson.toEntityIndexConfig();
        }
        catch ( IOException e )
        {
            throw new RepositoryException( "Failed to deserialize node indexConfig", e );
        }
    }
}
