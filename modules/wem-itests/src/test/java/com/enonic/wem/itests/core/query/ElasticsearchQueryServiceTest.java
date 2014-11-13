package com.enonic.wem.itests.core.query;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.core.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.itests.core.entity.AbstractNodeTest;
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodePath;
import com.enonic.wem.repo.NodeVersionId;

import static org.junit.Assert.*;

public class ElasticsearchQueryServiceTest
    extends AbstractNodeTest
{
    private ElasticsearchQueryService queryService;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.queryService = new ElasticsearchQueryService();
        this.queryService.setElasticsearchDao( this.elasticsearchDao );
    }

    @Test
    public void get_path_has_read()
        throws Exception
    {
        final User me = User.create().
            email( "rmy@enonic.com" ).
            login( "rmy" ).
            displayName( "Runar Myklebust" ).
            key( PrincipalKey.from( "system:user:rmy" ) ).
            build();

        final Principals principals = Principals.from( me );

        final IndexContext indexContext = IndexContext.create().
            workspace( ContentConstants.WORKSPACE_STAGE ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            principals( principals ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            accessControlList( AccessControlList.of( AccessControlEntry.create().
                principal( me.getKey() ).
                allow( Permission.READ ).
                build() ) ).
            build() );

        final NodeVersionId nodeVersionId = queryService.get( node.path(), indexContext );

        assertNotNull( nodeVersionId );
    }

    @Test
    public void anonymous_has_read()
        throws Exception
    {
        final Principals principals = Principals.empty();

        final IndexContext indexContext = IndexContext.create().
            workspace( ContentConstants.WORKSPACE_STAGE ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            principals( principals ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            accessControlList( AccessControlList.of( AccessControlEntry.create().
                principal( User.anonymous().getKey() ).
                allow( Permission.READ ).
                build() ) ).
            build() );

        final NodeVersionId nodeVersionId = queryService.get( node.path(), indexContext );

        assertNotNull( nodeVersionId );
    }

    @Test
    public void get_path_group_has_read()
        throws Exception
    {
        final User me = User.create().
            email( "rmy@enonic.com" ).
            login( "rmy" ).
            displayName( "Runar Myklebust" ).
            key( PrincipalKey.from( "system:user:rmy" ) ).
            build();

        final Group myGroup = Group.create().
            displayName( "My Group" ).
            key( PrincipalKey.from( "system:group:mygroup" ) ).
            build();

        final Principals principals = Principals.from( me, myGroup );

        final IndexContext indexContext = IndexContext.create().
            workspace( ContentConstants.WORKSPACE_STAGE ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            principals( principals ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            accessControlList( AccessControlList.of( AccessControlEntry.create().
                principal( myGroup.getKey() ).
                allow( Permission.READ ).
                build() ) ).
            build() );

        final NodeVersionId nodeVersionId = queryService.get( node.path(), indexContext );

        assertNotNull( nodeVersionId );
    }

    @Test
    public void get_path_no_read()
        throws Exception
    {
        final User me = User.create().
            email( "rmy@enonic.com" ).
            login( "rmy" ).
            displayName( "Runar Myklebust" ).
            key( PrincipalKey.from( "system:user:rmy" ) ).
            build();

        final Principals principals = Principals.from( me );

        final IndexContext indexContext = IndexContext.create().
            workspace( ContentConstants.WORKSPACE_STAGE ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            principals( principals ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            accessControlList( AccessControlList.of( AccessControlEntry.create().
                principal( me.getKey() ).
                allow( Permission.DELETE ).
                build() ) ).
            build() );

        final NodeVersionId nodeVersionId = queryService.get( node.path(), indexContext );

        assertNull( nodeVersionId );
    }

    @Test
    public void get_path_no_acl_for_node()
        throws Exception
    {
        final User me = User.create().
            email( "rmy@enonic.com" ).
            login( "rmy" ).
            displayName( "Runar Myklebust" ).
            key( PrincipalKey.from( "system:user:rmy" ) ).
            build();

        final Principals principals = Principals.from( me );

        final IndexContext indexContext = IndexContext.create().
            workspace( ContentConstants.WORKSPACE_STAGE ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            principals( principals ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final NodeVersionId nodeVersionId = queryService.get( node.path(), indexContext );

        assertNull( nodeVersionId );
    }

    @Test
    public void get_path_anonymous()
        throws Exception
    {
        final User anonymous = User.anonymous();

        final Principals principals = Principals.from( anonymous );

        final IndexContext indexContext = IndexContext.create().
            workspace( ContentConstants.WORKSPACE_STAGE ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            principals( principals ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            accessControlList( AccessControlList.of( AccessControlEntry.create().
                principal( anonymous.getKey() ).
                allow( Permission.READ ).
                build() ) ).
            build() );

        final NodeVersionId nodeVersionId = queryService.get( node.path(), indexContext );

        assertNotNull( nodeVersionId );
    }
}
