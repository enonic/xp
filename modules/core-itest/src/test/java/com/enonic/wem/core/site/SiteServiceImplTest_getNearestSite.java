package com.enonic.wem.core.site;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.site.CreateSiteParams;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.content.site.SiteConfigs;

import static org.junit.Assert.*;

public class SiteServiceImplTest_getNearestSite
    extends AbstractSiteServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void child_of_site()
        throws Exception
    {
        final Content site = createSite();

        final Content child = createContent( site.getPath() );

        final Site fetchedSite = this.siteService.getNearestSite( child.getId() );

        assertNotNull( fetchedSite );
        assertEquals( site.getId(), fetchedSite.getId() );

    }

    @Test
    public void is_site()
        throws Exception
    {
        final Content site = createSite();

        final Site fetchedSite = this.siteService.getNearestSite( site.getId() );

        assertNotNull( fetchedSite );
        assertEquals( site.getId(), fetchedSite.getId() );

    }

    @Test
    public void no_site_in_path()
        throws Exception
    {
        final Content content = createContent( ContentPath.ROOT );

        final Site fetchedSite = this.siteService.getNearestSite( content.getId() );
        assertNull( fetchedSite );
    }

    @Test
    public void deep_child_of_site()
        throws Exception
    {
        final Content site = createSite();

        final Content childLevel1 = createContent( site.getPath() );
        final Content childLevel2 = createContent( childLevel1.getPath() );
        final Content childLevel3 = createContent( childLevel2.getPath() );

        final Site fetchedSite = this.siteService.getNearestSite( childLevel3.getId() );

        assertNotNull( fetchedSite );
        assertEquals( site.getId(), fetchedSite.getId() );
    }

    private Content createSite()
        throws Exception
    {
        final CreateSiteParams createSiteParams = new CreateSiteParams();
        createSiteParams.parent( ContentPath.ROOT ).
            displayName( "My mock site" ).
            description( "This is my mock site" ).
            siteConfigs( SiteConfigs.empty() );

        return this.siteService.create( createSiteParams );
    }
}
