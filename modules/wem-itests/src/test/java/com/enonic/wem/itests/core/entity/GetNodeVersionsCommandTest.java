package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.FindNodeVersionsResult;
import com.enonic.wem.core.entity.GetNodeVersionsCommand;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.repository.StorageNameResolver;

import static junit.framework.Assert.assertEquals;

public class GetNodeVersionsCommandTest
    extends AbstractNodeTest
{

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        createContentRepository();
    }


    @Test
    public void testName()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        printAllIndexContent( StorageNameResolver.resolveStorageIndexName( ContentConstants.CONTENT_REPO.getId() ),
                              IndexType.VERSION.getName() );

        final FindNodeVersionsResult result = GetNodeVersionsCommand.create().
            from( 0 ).
            size( 100 ).
            nodeId( node.id() ).
            versionService( this.versionService ).
            build().
            execute();

        assertEquals( 1, result.getHits() );


    }
}
