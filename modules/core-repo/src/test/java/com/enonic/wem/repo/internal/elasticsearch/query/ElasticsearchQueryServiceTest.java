package com.enonic.wem.repo.internal.elasticsearch.query;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;
import com.enonic.wem.repo.internal.index.IndexContext;

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
        final IndexContext indexContext = IndexContext.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO.getId() ).
            principalsKeys( PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ) ) ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:system:rmy" ) ).
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
        final IndexContext indexContext = IndexContext.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO.getId() ).
            principalsKeys( PrincipalKeys.empty() ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                principal( PrincipalKey.ofAnonymous() ).
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

        final IndexContext indexContext = IndexContext.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO.getId() ).
            principalsKeys( PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ), PrincipalKey.from( "group:system:mygroup" ) ) ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                principal( PrincipalKey.from( "group:system:mygroup" ) ).
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
        final IndexContext indexContext = IndexContext.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO.getId() ).
            principalsKeys( PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ) ) ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:system:rmy" ) ).
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
        final IndexContext indexContext = IndexContext.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO.getId() ).
            principalsKeys( PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ) ) ).
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
        final IndexContext indexContext = IndexContext.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO.getId() ).
            principalsKeys( PrincipalKeys.from( PrincipalKey.ofAnonymous() ) ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                principal( PrincipalKey.ofAnonymous() ).
                allow( Permission.READ ).
                build() ) ).
            build() );

        final NodeVersionId nodeVersionId = queryService.get( node.path(), indexContext );

        assertNotNull( nodeVersionId );
    }
}
