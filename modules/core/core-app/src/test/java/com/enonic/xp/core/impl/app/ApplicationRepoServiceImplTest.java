package com.enonic.xp.core.impl.app;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationRepoServiceImplTest
{
    private static final String ROOT_TEST_PATH = "src/test/resources";

    private final NodeService nodeService = Mockito.mock( NodeService.class );

    private ApplicationRepoServiceImpl service;

    @BeforeEach
    void setUp()
    {
        this.service = new ApplicationRepoServiceImpl( this.nodeService );
    }

    @Test
    void create_node()
    {
        final AppInfo app = createApp();

        this.service.upsertApplicationNode( app, ByteSource.wrap( "myBinary".getBytes() ) );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).create( Mockito.isA( CreateNodeParams.class ) );
    }

    @Test
    void update_node()
    {
        final AppInfo app = createApp();

        Mockito.when(
                this.nodeService.getByPath( new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( "myBundle" ) ) ) )
            .thenReturn(
                Node.create().id( new NodeId() ).name( "myBundle" ).parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).build() );

        this.service.upsertApplicationNode( app, ByteSource.wrap( "myBinary".getBytes() ) );

        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).update( Mockito.isA( UpdateNodeParams.class ) );
    }

    @Test
    void delete_node()
    {
        final AppInfo app = createApp();

        Mockito.when( this.nodeService.getByPath( new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( "myBundle" ) ) ) )
            .thenReturn(
                Node.create().id( new NodeId() ).name( "myBundle" ).parentPath( ApplicationRepoServiceImpl.APPLICATION_PATH ).build() );

        this.service.deleteApplicationNode( ApplicationKey.from( app.name ) );

        ArgumentCaptor<DeleteNodeParams> argCaptor = ArgumentCaptor.forClass( DeleteNodeParams.class );
        Mockito.verify( this.nodeService, Mockito.times( 1 ) ).delete( argCaptor.capture() );
        assertEquals( new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( "myBundle" ) ),
                      argCaptor.getValue().getNodePath() );
    }

    private AppInfo createApp()
    {
        var app =  new AppInfo();
        app.name = "myBundle";
        return app;
    }
}
