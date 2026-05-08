package com.enonic.xp.core.impl.content;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsService;

import static com.enonic.xp.context.ContextAccessor.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LayersContentServiceTest
{
    private NodeService nodeService;

    private LayersContentService service;

    @BeforeEach
    void setUp()
    {
        nodeService = Mockito.mock( NodeService.class );

        final ContentTypeService contentTypeService = Mockito.mock( ContentTypeService.class );
        final EventPublisher eventPublisher = Mockito.mock( EventPublisher.class );
        final MixinService mixinService = Mockito.mock( MixinService.class );
        final CmsService cmsService = Mockito.mock( CmsService.class );
        final PageDescriptorService pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        final PartDescriptorService partDescriptorService = Mockito.mock( PartDescriptorService.class );
        final LayoutDescriptorService layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        final ContentConfig config = Mockito.mock( ContentConfig.class );

        service = new LayersContentService( nodeService, contentTypeService, eventPublisher, mixinService, cmsService,
                                            pageDescriptorService, partDescriptorService, layoutDescriptorService, config );
    }

    @Test
    void getById_setsPrimarySearchPreferenceInContext()
    {
        final AtomicReference<String> searchPreference = new AtomicReference<>();

        when( nodeService.getById( any() ) ).thenAnswer( invocation -> {
            searchPreference.set( (String) current().getAttribute( "searchPreference" ) );
            throw new NodeNotFoundException( "not found" );
        } );

        ContextBuilder.create().repositoryId( "repo" ).branch( "draft" ).build()
            .runWith( () -> service.getById( ContentId.from( "id" ) ) );

        assertEquals( "PRIMARY", searchPreference.get() );
    }

    @Test
    void find_setsPrimarySearchPreferenceInContext()
    {
        final AtomicReference<String> searchPreference = new AtomicReference<>();

        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenAnswer( invocation -> {
            searchPreference.set( (String) current().getAttribute( "searchPreference" ) );
            return FindNodesByQueryResult.create().build();
        } );

        ContextBuilder.create().repositoryId( "repo" ).branch( "draft" ).build()
            .runWith( () -> service.find( ContentQuery.create().build() ) );

        assertEquals( "PRIMARY", searchPreference.get() );
    }
}
