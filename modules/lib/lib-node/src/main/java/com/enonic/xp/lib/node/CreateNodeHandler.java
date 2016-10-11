package com.enonic.xp.lib.node;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.script.ScriptValue;

@SuppressWarnings("unused")
public class CreateNodeHandler
    extends BaseContextHandler
{
    private Map<String, Object> params;

    @Override
    protected Object doExecute()
    {
        validateRepo();

        final CreateNodeParams createNodeParams;

        createNodeParams = new CreateNodeParamsFactory().create( toPropertyTree( this.params ) );
        final Node node = this.nodeService.create( createNodeParams );
        return new NodeMapper( node );
    }

    private void validateRepo()
    {
        final RepositoryId repoId = ContextAccessor.current().getRepositoryId();

        final Repository repository = this.repositoryService.get( repoId );

        if ( repository == null )
        {
            throw new RepositoryNotFoundException( "Repository with id [" + repoId + "] not found" );
        }
    }

    private PropertyTree toPropertyTree( final Map<String, Object> params )
    {
        return new JsonToPropertyTreeTranslator().translate( createJson( params ) );
    }


    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }

    @SuppressWarnings("unused")
    public void setParams( final ScriptValue params )
    {
        this.params = params.getMap();
    }
}
