package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class AccessControlTest
    extends AbstractNodeTest
{
    private NodeServiceImpl nodeService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexServiceInternal );
        this.nodeService.setQueryService( queryService );
        this.nodeService.setNodeDao( nodeDao );
        this.nodeService.setVersionService( versionService );
        this.nodeService.setBranchService( branchService );

        this.createDefaultRootNode();
    }

    @Test
    public void index_has_read()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:myuserstore:rmy" ) ).
                allow( Permission.READ ).
                build() ).
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:myuserstore:tsi" ) ).
                allow( Permission.READ ).
                build() ).
            build();

        final CreateNodeParams params = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( aclList ).
            build();

        final Node node = this.nodeService.create( params );
        refresh();
    }

}
