package com.enonic.xp.lib.node;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.script.ScriptValue;

public class CreateNodeHandler
    extends BaseContextHandler
{
    private Map<String, Object> params;

    @Override
    protected Object doExecute()
    {
        final CreateNodeParams createNodeParams;

            createNodeParams = new CreateNodeParamsFactory().create( toPropertyTree( this.params ) );
        final Node node = this.nodeService.create( createNodeParams );
        return new NodeMapper( node );
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
