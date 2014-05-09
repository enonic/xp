package com.enonic.wem.core.elasticsearch;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.json.NodeJsonSerializer;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

class NodeStorageDocumentFactory
{

    public static final String PATH = "path";

    public static final String PARENT_PATH = "parent_path";

    public static final String ENTITY = "entity";

    static NodeStorageDocument create( final Node node )
    {
        final String serializedNode = NodeJsonSerializer.toString( node );

        // TODO: How to handle id?

        return NodeStorageDocument.newDocument().index( Index.STORE ).
            indexType( IndexType.ENTITY ).
            id( node.id().toString() ).
            add( PATH, node.path().toString() ).
            add( PARENT_PATH, node.parent().toString() ).
            add( ENTITY, serializedNode ).build();
    }

    static NodeStorageDocument update( final Node node )
    {
        final String serializedNode = NodeJsonSerializer.toString( node );

        return NodeStorageDocument.newDocument().index( Index.STORE ).
            indexType( IndexType.ENTITY ).
            id( node.id().toString() ).
            add( PATH, node.path().toString() ).
            add( PARENT_PATH, node.parent().toString() ).
            add( ENTITY, serializedNode ).build();
    }

}
