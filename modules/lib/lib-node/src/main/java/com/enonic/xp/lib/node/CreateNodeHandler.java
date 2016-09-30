package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.script.ScriptValue;

public class CreateNodeHandler
    extends BaseContextHandler
{
    private ScriptValue params;

    @Override
    protected Object doExecute()
    {
        final CreateNodeParams createNodeParams;

        createNodeParams = new CreateNodeParamsFactory().create( params );
        final Node node = this.nodeService.create( createNodeParams );
        return new NodeMapper( node );
    }

    @SuppressWarnings("unused")
    public void setParams( final ScriptValue params )
    {
        this.params = params;
    }
}
