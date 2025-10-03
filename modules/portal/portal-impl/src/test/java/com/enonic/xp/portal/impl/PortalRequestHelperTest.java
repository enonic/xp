package com.enonic.xp.portal.impl;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PortalRequestHelperTest
{
    @Test
    void testGetSiteRelativePath()
    {
        final PortalRequest portalRequest = new PortalRequest();

        // case 1
        portalRequest.setContentPath( null );
        assertEquals( "/", PortalRequestHelper.getSiteRelativePath( portalRequest ) );

        // case 2
        portalRequest.setContentPath( ContentPath.ROOT );
        assertEquals( "/", PortalRequestHelper.getSiteRelativePath( portalRequest ) );

        // case 3
        portalRequest.setContentPath( ContentPath.from( "/contentPath" ) );
        portalRequest.setContent( null );
        assertEquals( "/contentPath", PortalRequestHelper.getSiteRelativePath( portalRequest ) );

        // case 4
        final Content content = newContent();
        portalRequest.setContentPath( content.getPath() );
        portalRequest.setContent( content );
        assertEquals( content.getPath().toString(), PortalRequestHelper.getSiteRelativePath( portalRequest ) );

        // case 5
        final Site site = newSite();
        portalRequest.setSite( site );
        assertEquals( "/landing-page", PortalRequestHelper.getSiteRelativePath( portalRequest ) );

        // case 6
        portalRequest.setContentPath( site.getPath() );
        portalRequest.setContent( site );
        portalRequest.setSite( site );
        assertEquals( "/", PortalRequestHelper.getSiteRelativePath( portalRequest ) );

        // case 7
        portalRequest.setMode( RenderMode.EDIT );
        portalRequest.setContentPath( ContentPath.from( "/contentPath" ) );
        portalRequest.setContent( null );
        portalRequest.setSite( null );
        assertEquals( "/contentPath", PortalRequestHelper.getSiteRelativePath( portalRequest ) );


        // case 8
        portalRequest.setMode( RenderMode.EDIT );
        portalRequest.setContentPath( ContentPath.from( "/" ) );
        portalRequest.setContent( Content.create( content ).root().build() );
        portalRequest.setSite( null );
        assertEquals( "/", PortalRequestHelper.getSiteRelativePath( portalRequest ) );

        // case 9
        portalRequest.setMode( RenderMode.EDIT );
        portalRequest.setContentPath( ContentPath.from( "/contentPath" ) );
        portalRequest.setContent( content );
        portalRequest.setSite( site );
        assertEquals( "/landing-page", PortalRequestHelper.getSiteRelativePath( portalRequest ) );
    }

    private Content newContent()
    {
        final Content.Builder<?> builder = Content.create();
        builder.id( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) );
        builder.name( "landing-page" );
        builder.displayName( "My Landing Page" );
        builder.parentPath( ContentPath.from( "/mysite" ) );
        builder.type( ContentTypeName.from( ApplicationKey.from( "com.enonic.test.app" ), "landing-page" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        builder.permissions( AccessControlList.create()
                                 .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                                 .build() );
        return builder.build();
    }

    private Site newSite()
    {

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "site0c10-0002-4b68-b407-87412f3e45c9" ) );
        site.data( new PropertyTree() );
        site.name( "mysite" );
        site.parentPath( ContentPath.ROOT );
        site.permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() );
        return site.build();
    }
}
