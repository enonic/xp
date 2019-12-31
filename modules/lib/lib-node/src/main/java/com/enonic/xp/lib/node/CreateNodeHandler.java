package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.script.ScriptValue;

@SuppressWarnings("unused")
public class CreateNodeHandler
    extends AbstractNodeHandler
{
    private final ScriptValue params;

    public CreateNodeHandler( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    @Override
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractNodeHandler.Builder<Builder>
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
