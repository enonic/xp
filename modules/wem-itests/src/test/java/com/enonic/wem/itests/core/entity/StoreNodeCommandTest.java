package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.StoreNodeCommand;
import com.enonic.wem.core.repository.IndexNameResolver;
import com.enonic.wem.core.repository.RepositoryInitializer;

public class StoreNodeCommandTest
    extends AbstractNodeTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        RepositoryInitializer repositoryInitializer = new RepositoryInitializer();
        repositoryInitializer.setIndexService( this.indexService );

        repositoryInitializer.init( ContentConstants.CONTENT_REPO );
    }


    @Test
    public void with_acl()
        throws Exception
    {

        Node newNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        Node updatedNode = Node.newNode( newNode ).
            accessControlList( AccessControlList.create().
                add( AccessControlEntry.create().
                    allow( Permission.READ ).
                    allow( Permission.CREATE ).
                    allow( Permission.DELETE ).
                    principal( PrincipalKey.from( "myuserstore:user:rmy" ) ).
                    build() ).
                build() ).
            childOrder( ChildOrder.from( "name DESC" ) ).
            build();

        refresh();

        StoreNodeCommand.create().
            node( newNode ).
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            build().
            execute();

        refresh();

        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( ContentConstants.CONTENT_REPO.getId() ), "stage" );

    }
}
