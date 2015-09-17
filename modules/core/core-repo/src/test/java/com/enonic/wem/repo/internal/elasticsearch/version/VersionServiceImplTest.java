package com.enonic.wem.repo.internal.elasticsearch.version;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.Assert.*;

public class VersionServiceImplTest
{

    private VersionServiceImpl versionService;

    private StorageDao storageDao;

    private InternalContext context;

    @Before
    public void setUp()
        throws Exception
    {
        storageDao = Mockito.mock( StorageDao.class );
        this.versionService = new VersionServiceImpl();
        this.versionService.setStorageDao( this.storageDao );

        this.context = InternalContext.create().
            repositoryId( RepositoryId.from( "myRepo" ) ).
            branch( Branch.from( "myBranch" ) ).
            build();
    }


    @Test
    public void store()
        throws Exception
    {
        final NodeVersionId nodeVersion = NodeVersionId.from( "myNodeVersion" );
        final Instant now = Instant.now();
        final NodeId nodeId = NodeId.from( "myNodeId" );
        final NodePath nodePath = NodePath.ROOT;

        this.versionService.store( NodeVersionDocument.create().
            nodeVersionId( nodeVersion ).
            timestamp( now ).
            nodeId( nodeId ).
            nodePath( nodePath ).
            build(), context );

        final NodeVersion myNodeVersion = this.versionService.getVersion( nodeVersion, this.context );

        assertNotNull( myNodeVersion );
        assertEquals( now, myNodeVersion.getTimestamp() );
        assertEquals( nodeId, myNodeVersion.getNodeId() );
        assertEquals( nodePath, myNodeVersion.getNodePath() );
        assertEquals( nodeVersion, myNodeVersion.getNodeVersionId() );

    }
}