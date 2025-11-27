package com.enonic.xp.portal.impl.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorChainResolverTest
{
    private PortalRequest portalRequest;

    private SiteService siteService;

    private ProcessorChainResolver resolver;

    @BeforeEach
    void before()
    {
        portalRequest = new PortalRequest();
        siteService = Mockito.mock( SiteService.class );
        resolver = new ProcessorChainResolver();
        resolver.setSiteService( siteService );
    }

    @Test
    void testFilterChainResolver()
    {
        ApplicationKey ak1 = ApplicationKey.from( "ak1" );
        SiteConfig sc1 = SiteConfig.create().application( ak1 ).config( new PropertyTree() ).build();
        ApplicationKey ak2 = ApplicationKey.from( "ak2" );
        SiteConfig sc2 = SiteConfig.create().application( ak2 ).config( new PropertyTree() ).build();
        ApplicationKey ak3 = ApplicationKey.from( "ak3" );
        SiteConfig sc3 = SiteConfig.create().application( ak3 ).config( new PropertyTree() ).build();

        final PropertyTree siteData = new PropertyTree();

        PropertySet parentSet2 = siteData.getRoot();
        final PropertySet siteConfigAsSet2 = parentSet2.addSet( "siteConfig" );
        siteConfigAsSet2.addString( "applicationKey", sc1.getApplicationKey().toString() );
        siteConfigAsSet2.addSet( "config", sc1.getConfig().getRoot().copy( parentSet2.getTree() ) );
        PropertySet parentSet1 = siteData.getRoot();
        final PropertySet siteConfigAsSet1 = parentSet1.addSet( "siteConfig" );
        siteConfigAsSet1.addString( "applicationKey", sc2.getApplicationKey().toString() );
        siteConfigAsSet1.addSet( "config", sc2.getConfig().getRoot().copy( parentSet1.getTree() ) );
        PropertySet parentSet = siteData.getRoot();
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", sc3.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", sc3.getConfig().getRoot().copy( parentSet.getTree() ) );

        portalRequest.setSite( Site.create().data( siteData ).name( "Site" ).path( "/site" ).parentPath( ContentPath.ROOT ).build() );

        ResponseProcessorDescriptor fd12 = ResponseProcessorDescriptor.create().application( ak1 ).name( "ak1-2" ).order( 2 ).build();
        ResponseProcessorDescriptor fd13 = ResponseProcessorDescriptor.create().application( ak1 ).name( "ak1-3" ).order( 3 ).build();
        ResponseProcessorDescriptor fd15 = ResponseProcessorDescriptor.create().application( ak1 ).name( "ak1-5" ).order( 5 ).build();

        SiteDescriptor sd1 = SiteDescriptor.create()
            .applicationKey( ak1 )
            .responseProcessors( ResponseProcessorDescriptors.from( fd13, fd15, fd12 ) )
            .build();
        Mockito.when( siteService.getDescriptor( Mockito.eq( ak1 ) ) ).thenReturn( sd1 );

        ResponseProcessorDescriptor fd21 = ResponseProcessorDescriptor.create().application( ak2 ).name( "ak2-1" ).order( 1 ).build();
        ResponseProcessorDescriptor fd22 = ResponseProcessorDescriptor.create().application( ak2 ).name( "ak1-2" ).order( 2 ).build();
        ResponseProcessorDescriptor fd23 = ResponseProcessorDescriptor.create().application( ak2 ).name( "ak2-3" ).order( 3 ).build();

        SiteDescriptor sd2 = SiteDescriptor.create()
            .applicationKey( ak2 )
            .responseProcessors( ResponseProcessorDescriptors.from( fd22, fd23, fd21 ) )
            .build();
        Mockito.when( siteService.getDescriptor( Mockito.eq( ak2 ) ) ).thenReturn( sd2 );

        ResponseProcessorDescriptor fd32 = ResponseProcessorDescriptor.create().application( ak3 ).name( "ak3-2" ).order( 2 ).build();

        SiteDescriptor sd3 =
            SiteDescriptor.create().applicationKey( ak3 ).responseProcessors( ResponseProcessorDescriptors.from( fd32 ) ).build();
        Mockito.when( siteService.getDescriptor( Mockito.eq( ak3 ) ) ).thenReturn( sd3 );

        ResponseProcessorDescriptors filters = resolver.resolve( portalRequest );

        assertEquals( 7, filters.getSize() );

        ResponseProcessorDescriptor fd = filters.get( 0 );
        assertEquals( 1, fd.getOrder() );
        assertEquals( ak2, fd.getApplication() );

        fd = filters.get( 1 );
        assertEquals( 2, fd.getOrder() );
        assertEquals( ak1, fd.getApplication() );

        fd = filters.get( 2 );
        assertEquals( 2, fd.getOrder() );
        assertEquals( ak2, fd.getApplication() );

        fd = filters.get( 3 );
        assertEquals( 2, fd.getOrder() );
        assertEquals( ak3, fd.getApplication() );

        fd = filters.get( 4 );
        assertEquals( 3, fd.getOrder() );
        assertEquals( ak1, fd.getApplication() );

        fd = filters.get( 5 );
        assertEquals( 3, fd.getOrder() );
        assertEquals( ak2, fd.getApplication() );

        fd = filters.get( 6 );
        assertEquals( 5, fd.getOrder() );
        assertEquals( ak1, fd.getApplication() );
    }

    @Test
    void testEmptySite()
    {
        ResponseProcessorDescriptors filters = resolver.resolve( portalRequest );
        assertEquals( 0, filters.getSize() );
    }
}
