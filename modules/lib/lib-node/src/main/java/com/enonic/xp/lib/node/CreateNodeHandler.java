package com.enonic.xp.lib.node;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.script.ScriptValue;

@SuppressWarnings("unused")
public class CreateNodeHandler
    extends BaseNodeHandler
{
    private final ScriptValue params;

    public CreateNodeHandler( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public Object execute()
    {
        final ScriptValueTranslatorResult params = getParams( this.params );
        final CreateNodeParams createNodeParams = new CreateNodeParamsFactory().create( params );
        final Node node = this.nodeService.create( createNodeParams );
        return new NodeMapper( node );
    }

    private ScriptValueTranslatorResult getParams( final ScriptValue params )
    {
        return new ScriptValueTranslator().create( params );
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseNodeHandler.Builder<Builder>
    {
        private ScriptValue params;

        private Builder()
        {
        }

        public Builder params( final ScriptValue val )
        {
            params = val;
            return this;
        }

        public CreateNodeHandler build()
        {
            return new CreateNodeHandler( this );
        }
    }

}
