package com.enonic.xp.core.impl.site;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.site.CreateSiteParams;
import com.enonic.xp.content.site.ModuleConfigs;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.site.SiteDescriptor;

import static org.junit.Assert.*;

public class SiteServiceImplTest
{
    private static final String MODULE_NAME = "mymodule";

    private static final String UNKNOWN_MODULE_NAME = "unknownmodule";

    private static final ContentId CONTENT_ID = ContentId.from( "aaa" );


    private ModuleKey moduleKey;

    private SiteDescriptor mockedSiteDescriptor;

    private Site site;

    private SiteServiceImpl siteService;

    @Before
    public void before()
    {
        moduleKey = ModuleKey.from( MODULE_NAME );

        //Creates a mocked SiteDescriptor
        mockedSiteDescriptor = SiteDescriptor.create().build();

        //Creates a mocked SiteDescriptorRegistry
        SiteDescriptorRegistry siteDescriptorRegistry = Mockito.mock( SiteDescriptorRegistry.class );
        Mockito.when( siteDescriptorRegistry.get( moduleKey ) ).thenReturn( mockedSiteDescriptor );

        //Creates a mocked ContentService
        ContentService contentService = Mockito.mock( ContentService.class );
        site = Site.newSite().
            path( "/mycontent" ).
            id( CONTENT_ID ).
            build();
        Mockito.when( contentService.getById( CONTENT_ID ) ).thenReturn( site );
        Mockito.when( contentService.create( Mockito.isA( CreateContentParams.class ) ) ).thenReturn( site );

        //Creates the service to test
        siteService = new SiteServiceImpl();
        siteService.setSiteDescriptorRegistry( siteDescriptorRegistry );
        siteService.setContentService( contentService );
    }

    @Test
    public void get_descriptor()
    {
        final SiteDescriptor siteDescriptor = siteService.getDescriptor( moduleKey );
        assertEquals( mockedSiteDescriptor, siteDescriptor );
    }

    @Test
    public void get_descriptor_for_unknown_module()
    {
        final SiteDescriptor siteDescriptor = siteService.getDescriptor( ModuleKey.from( UNKNOWN_MODULE_NAME ) );
        assertEquals( null, siteDescriptor );
    }

    @Test
    public void get_nearest_site()
        throws Exception
    {
        final Site nearestSite = siteService.getNearestSite( CONTENT_ID );
        assertEquals( site, nearestSite );
    }

    @Test
    public void create()
        throws Exception
    {
        final CreateSiteParams createSiteParams = new CreateSiteParams();
        createSiteParams.parent( ContentPath.ROOT ).
            displayName( "My site" ).
            description( "This is my site" ).
            moduleConfigs( ModuleConfigs.empty() );

        final Content content = this.siteService.create( createSiteParams );
        assertEquals( site, content );
    }


}
