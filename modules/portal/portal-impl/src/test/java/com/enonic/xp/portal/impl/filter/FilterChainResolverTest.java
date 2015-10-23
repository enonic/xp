package com.enonic.xp.portal.impl.filter;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.filter.FilterDescriptor;
import com.enonic.xp.site.filter.FilterDescriptors;

import static org.junit.Assert.*;

public class FilterChainResolverTest
{
    private PortalRequest portalRequest;

    private SiteService siteService;

    private FilterChainResolver resolver;

    @Before
    public void before()
    {
        portalRequest = new PortalRequest();
        siteService = Mockito.mock( SiteService.class );
        resolver = new FilterChainResolver();
        resolver.setSiteService( siteService );
    }

    @Test
    public void testFilterChainResolver()
    {
        ApplicationKey ak1 = ApplicationKey.from( "ak1" );
        SiteConfig sc1 = SiteConfig.create().application( ak1 ).config( new PropertyTree() ).build();
        ApplicationKey ak2 = ApplicationKey.from( "ak2" );
        SiteConfig sc2 = SiteConfig.create().application( ak2 ).config( new PropertyTree() ).build();
        ApplicationKey ak3 = ApplicationKey.from( "ak3" );
        SiteConfig sc3 = SiteConfig.create().application( ak3 ).config( new PropertyTree() ).build();

        portalRequest.setSite( Site.create().
            siteConfigs( SiteConfigs.from( sc1, sc2, sc3 ) ).
            name( "Site" ).
            path( "/site" ).
            parentPath( ContentPath.ROOT ).
            build() );

        FilterDescriptor fd12 = FilterDescriptor.create().application( ak1 ).name( "ak1-2" ).order( 2 ).build();
        FilterDescriptor fd13 = FilterDescriptor.create().application( ak1 ).name( "ak1-3" ).order( 3 ).build();
        FilterDescriptor fd15 = FilterDescriptor.create().application( ak1 ).name( "ak1-5" ).order( 5 ).build();

        SiteDescriptor sd1 = SiteDescriptor.create().
            filterDescriptors( FilterDescriptors.from( fd13, fd15, fd12 ) ).
            build();
        Mockito.when( siteService.getDescriptor( Mockito.eq( ak1 ) ) ).thenReturn( sd1 );

        FilterDescriptor fd21 = FilterDescriptor.create().application( ak2 ).name( "ak2-1" ).order( 1 ).build();
        FilterDescriptor fd22 = FilterDescriptor.create().application( ak2 ).name( "ak1-2" ).order( 2 ).build();
        FilterDescriptor fd23 = FilterDescriptor.create().application( ak2 ).name( "ak2-3" ).order( 3 ).build();

        SiteDescriptor sd2 = SiteDescriptor.create().
            filterDescriptors( FilterDescriptors.from( fd22, fd23, fd21 ) ).
            build();
        Mockito.when( siteService.getDescriptor( Mockito.eq( ak2 ) ) ).thenReturn( sd2 );

        FilterDescriptor fd32 = FilterDescriptor.create().application( ak3 ).name( "ak3-2" ).order( 2 ).build();

        SiteDescriptor sd3 = SiteDescriptor.create().
            filterDescriptors( FilterDescriptors.from( fd32 ) ).
            build();
        Mockito.when( siteService.getDescriptor( Mockito.eq( ak3 ) ) ).thenReturn( sd3 );

        List<FilterDescriptor> filters = resolver.resolve( portalRequest );

        assertEquals( 7, filters.size() );

        FilterDescriptor fd = filters.get( 0 );
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
}
