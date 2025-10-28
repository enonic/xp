package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.site.Site;

import static org.mockito.Mockito.when;

class GetSiteHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final Site site = TestDataFixtures.newSite();
        when( this.contentService.findNearestSiteByPath( Mockito.any() ) ).thenReturn( site );
        when( this.contentService.getNearestSite( Mockito.any() ) ).thenReturn( site );

        runScript( "/lib/xp/examples/content/getSite.js" );
    }
}
