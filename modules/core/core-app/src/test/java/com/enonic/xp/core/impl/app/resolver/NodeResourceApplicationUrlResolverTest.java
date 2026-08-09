package com.enonic.xp.core.impl.app.resolver;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.ListNodesByParentParams;
import com.enonic.xp.node.ListNodesByParentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeResourceApplicationUrlResolverTest
{
    private static final ApplicationKey APP_KEY = ApplicationKey.from( "myapp" );

    @Mock
    private NodeService nodeService;

    private NodeResourceApplicationUrlResolver resolver;

    @BeforeEach
    void setup()
    {
        this.resolver = new NodeResourceApplicationUrlResolver( APP_KEY, this.nodeService );
    }

    @Test
    void findFiles_lists_the_cms_subtree()
    {
        when( this.nodeService.list( any() ) ).thenReturn( result( "/myapp/cms/content-types/mytype/content-types" ) );

        this.resolver.findFiles();

        final ArgumentCaptor<ListNodesByParentParams> params = ArgumentCaptor.forClass( ListNodesByParentParams.class );
        verify( this.nodeService ).list( params.capture() );

        assertEquals( new NodePath( "/myapp/cms" ), params.getValue().getParentPath() );
        assertTrue( params.getValue().isRecursive() );
    }

    @Test
    void findFiles_returns_resources_relative_to_the_application()
    {
        when( this.nodeService.list( any() ) ).thenReturn(
            result( "/myapp/cms/content-types/mytype/content-types", "/myapp/cms/parts/mypart/parts" ) );

        assertEquals( Set.of( "/cms/content-types/mytype/content-types", "/cms/parts/mypart/parts" ), this.resolver.findFiles() );
    }

    @Test
    void findFiles_skips_the_folders_on_the_way_to_a_resource()
    {
        when( this.nodeService.list( any() ) ).thenReturn(
            result( "/myapp/cms/content-types", "/myapp/cms/content-types/mytype", "/myapp/cms/content-types/mytype/content-types" ) );

        assertEquals( Set.of( "/cms/content-types/mytype/content-types" ), this.resolver.findFiles() );
    }

    private static ListNodesByParentResult result( final String... paths )
    {
        final ListNodesByParentResult.Builder builder = ListNodesByParentResult.create();
        for ( final String path : paths )
        {
            builder.addEntry( new NodeListEntry( new NodeId(), new NodePath( path ), Instant.now() ) );
        }
        return builder.build();
    }
}
