package com.enonic.wem.core.entity.dao;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.EntityPatternIndexConfig;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.core.entity.EntityPatternIndexConfigJson;
import com.enonic.wem.core.entity.EntityPropertyIndexConfigJson;
import com.enonic.wem.core.entity.relationship.EntityIndexConfigJson;
import com.enonic.wem.core.support.dao.JsonHelper;

class IndexConfigJcrMapper
{
    private final static String INDEX_PROPERTY = "indexConfig";

    private final JsonHelper jsonHelper = new JsonHelper();


    void toJcr( final EntityIndexConfig indexConfig, final Node targetNode )
        throws RepositoryException
    {
        if ( indexConfig != null )
        {
            EntityIndexConfigJson json;

            if ( indexConfig instanceof EntityPropertyIndexConfig )
            {
                json = new EntityPropertyIndexConfigJson( (EntityPropertyIndexConfig) indexConfig );
            }
            else if ( indexConfig instanceof EntityPatternIndexConfig )
            {
                json = new EntityPatternIndexConfigJson( (EntityPatternIndexConfig) indexConfig );
            }
            else
            {
                throw new IllegalArgumentException(
                    "To JCR not implemented for EntityIndexConfig of type: " + indexConfig.getClass().getName() );
            }

            targetNode.setProperty( INDEX_PROPERTY, jsonHelper.objectToString( json ) );
        }
    }

    EntityIndexConfig toEntityIndexConfig( final Node sourceNode )
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
