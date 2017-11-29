package com.enonic.xp.core.content;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static org.junit.Assert.*;

public class ContentServiceImplTest_findByApplicationKey
    extends AbstractContentServiceTest
{
    @Test
    public void test()
        throws Exception
    {
        final ApplicationKey applicationKey1 = ApplicationKey.from( "app:key1" );
        final ApplicationKey applicationKey2 = ApplicationKey.from( "app:key2" );

        final Content site1 = createSite( "a", SiteConfigs.from(
            SiteConfig.create().application( applicationKey1 ).config( new PropertyTree() ).build() ) );

        final Content site1_2 = createSite(  "b", SiteConfigs.from(
            SiteConfig.create().application( applicationKey1 ).config( new PropertyTree() ).build(),
            SiteConfig.create().application( applicationKey2 ).config( new PropertyTree() ).build()) );

        final Content site2 = createSite( "c", SiteConfigs.from(
            SiteConfig.create().application( applicationKey2 ).config( new PropertyTree() ).build() ) );

        assertEquals( Contents.from( site1, site1_2 ).getIds(), contentService.findByApplicationKey( applicationKey1 ).getIds() );
        assertEquals( Contents.from( site1_2, site2 ).getIds(), contentService.findByApplicationKey( applicationKey2 ).getIds() );
    }

    private Site createSite( final String name, SiteConfigs siteConfigs )
    {
        return this.contentService.create( new CreateSiteParams().name( name ).displayName( name ).siteConfigs( siteConfigs ).
            parent( ContentPath.ROOT ) );
    }

}
