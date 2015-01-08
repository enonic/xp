package com.enonic.wem.repo.internal.elasticsearch.xcontent;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.repo.internal.index.IndexException;

public class NodeXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{
    public static XContentBuilder create( final Node node, final NodeVersionId nodeVersionId )
    {
        try
        {
            final XContentBuilder builder = startBuilder();
            addField( builder, "nodeid", node.id().toString() );
            addField( builder, "nodetype", node.getNodeType().toString() );
            addField( builder, "lastVersionId", nodeVersionId );
            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for NodeXContentBuilder", e );
        }

    }

}
