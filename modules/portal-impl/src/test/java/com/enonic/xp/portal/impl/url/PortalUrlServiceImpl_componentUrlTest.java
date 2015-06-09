package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.page.region.PartComponent;
import com.enonic.xp.page.region.Region;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.ComponentUrlParams;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_componentUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_toMeAndContextIsPage()
    {
        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest ).
            param( "a", 3 );

        final String url = this.service.componentUrl( params );
        assertEquals( "/portal/draft/context/path?a=3", url );
    }

    @Test
    public void createUrl_toMeAndContextIsComponent()
    {
        addComponent();

        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest );

        final String url = this.service.componentUrl( params );
        assertEquals( "/portal/draft/context/path/_/component/main/0", url );
    }

    @Test
    public void createUrl_toOtherComponentOnPage()
    {
        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest ).
            component( "other/1" );

        final String url = this.service.componentUrl( params );
        assertEquals( "/portal/draft/context/path/_/component/other/1", url );
    }

    @Test
    public void createUrl_toComponentOnOtherPageWithPath()
    {
        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest ).
            path( "/a/b" ).
            component( "other/1" );

        final String url = this.service.componentUrl( params );
        assertEquals( "/portal/draft/a/b/_/component/other/1", url );
    }

    @Test
    public void createUrl_toComponentOnOtherPageWithId()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final ComponentUrlParams params = new ComponentUrlParams().
            portalRequest( this.portalRequest ).
            id( "123456" ).
            component( "other/1" );

        final String url = this.service.componentUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/component/other/1", url );
    }

    private void addComponent()
    {
        final PartComponent component = PartComponent.
            newPartComponent().
            name( "mycomp" ).
            build();

        final Region region = Region.
            newRegion().
            name( "main" ).
            add( component ).
            build();

        component.setRegion( region );
        this.portalRequest.setComponent( component );
    }
}
