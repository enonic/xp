package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeSearchPreference;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
    void getById_usesPrimarySearchPreference_whenNotFound()
    {
        when( nodeService.getById( any(), eq( NodeSearchPreference.PRIMARY ) ) ).thenThrow( new NodeNotFoundException( "not found" ) );

        service.getById( ContentId.from( "id" ) );

        verify( nodeService ).getById( any(), eq( NodeSearchPreference.PRIMARY ) );
    }

    @Test
    void getById_usesPrimarySearchPreference_whenContentExists()
    {
        when( nodeService.getById( any(), eq( NodeSearchPreference.PRIMARY ) ) ).thenReturn( ContentFixture.someContentNode() );

        final boolean contentFound = service.getById( ContentId.from( "id" ) ).isPresent();

        assertTrue( contentFound );
        verify( nodeService ).getById( any(), eq( NodeSearchPreference.PRIMARY ) );
    }

    @Test
    void find_usesPrimarySearchPreference()
    {
        when( nodeService.findByQuery( any( NodeQuery.class ), eq( NodeSearchPreference.PRIMARY ) ) ).thenReturn(
            FindNodesByQueryResult.create().build() );

        service.find( ContentQuery.create().build() );

        verify( nodeService ).findByQuery( any( NodeQuery.class ), eq( NodeSearchPreference.PRIMARY ) );
    }
}
