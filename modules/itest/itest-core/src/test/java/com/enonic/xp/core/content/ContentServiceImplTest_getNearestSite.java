package com.enonic.xp.core.content;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContentServiceImplTest_getNearestSite
    extends AbstractContentServiceTest
{
    @Test
    void child_of_site()
    {
        final Content site = createSite();

        final Content child = createContent( site.getPath() );

        final Site fetchedSite = this.contentService.getNearestSite( child.getId() );

        assertNotNull( fetchedSite );
        assertEquals( site.getId(), fetchedSite.getId() );
    }

    @Test
    void is_site()
    {
        final Content site = createSite();

        final Site fetchedSite = this.contentService.getNearestSite( site.getId() );

        assertNotNull( fetchedSite );
        assertEquals( site.getId(), fetchedSite.getId() );
    }

    @Test
    void no_site_in_path()
    {
        final Content content = createContent( ContentPath.ROOT );

        final Site fetchedSite = this.contentService.getNearestSite( content.getId() );
        assertNull( fetchedSite );
    }

    @Test
    void deep_child_of_site()
    {
        final Content site = createSite();

        final Content childLevel1 = createContent( site.getPath() );
        final Content childLevel2 = createContent( childLevel1.getPath() );
        final Content childLevel3 = createContent( childLevel2.getPath() );

        final Site fetchedSite = this.contentService.getNearestSite( childLevel3.getId() );

        assertNotNull( fetchedSite );
        assertEquals( site.getId(), fetchedSite.getId() );
    }

    @Test
    void child_of_site_pending_publish_master()
    {
        final Content site = createSite();

        final Content child =
            createAndPublishContent( site.getPath(), Instant.now().plus( Duration.ofDays( 1 ) ) );
        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( site.getId() ) ).build() );

        final Site fetchedSite = ctxMaster().callWith( () -> this.contentService.getNearestSite( child.getId() ) );
        assertNull( fetchedSite );
    }

    @Test
    void deep_child_of_site_pending_publish_master()
    {
        final Content site = createSite();
        final Content childLevel1 = createContent( site.getPath() );
        final Content childLevel2 =
            createAndPublishContent( childLevel1.getPath(), Instant.now().plus( Duration.ofDays( 1 ) ) );
        final Content childLevel3 = createContent( childLevel2.getPath() );
        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( site.getId() ) ).build() );

        final Site fetchedSite = ctxMaster().callWith( () -> this.contentService.getNearestSite( childLevel3.getId() ) );
        assertNull( fetchedSite );
    }

    @Test
    void child_of_site_published_master()
    {
        final Content site = createSite();
        final Content child = createAndPublishContent( site.getPath(), Instant.now().plus( Duration.ofDays( 1 ) ), Instant.now().plus( Duration.ofDays( 2 ) ) );
        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( site.getId() ) ).build() );

        final Site fetchedSite = ctxMaster().callWith( () -> this.contentService.getNearestSite( child.getId() ) );
        assertNull( fetchedSite );
    }

    @Test
    void testPublishInfo()
    {
        final Content site = createSite();
        this.contentService.publish( PushContentParams.create()
                                         .contentIds( ContentIds.from( site.getId() ) )
                                         .publishFrom( Instant.parse( "2022-12-01T14:00:00.668487800Z" ) )
                                         .publishTo( Instant.parse( "2099-12-03T14:00:00.669487800Z" ) )
                                         .build() );

        final Content publishedContent = ctxMaster().callWith( () -> this.contentService.getById( site.getId() ) );
        assertNotNull( publishedContent );
        assertNotNull( publishedContent.getPublishInfo() );
        assertEquals( "2022-12-01T14:00:00.668Z", publishedContent.getPublishInfo().getFrom().toString() );
        assertEquals( "2099-12-03T14:00:00.669Z", publishedContent.getPublishInfo().getTo().toString() );
    }

    @Test
    void test_getNearestSite_WhenSomeParentInPathDoestNotHaveReadPermission()
    {
        final Content site = createSite();

        final Content childLevel1 = createContent( site.getPath(), "Child 1", AccessControlList.create()
            .add( AccessControlEntry.create().principal( RoleKeys.AUTHENTICATED ).allowAll().build() )
            .add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allowAll().deny( Permission.READ ).build() )
            .build() );
        final Content childLevel2 = createContent( childLevel1.getPath(), "Child 2", AccessControlList.create()
            .add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build() )
            .build() );

        final Site fetchedSite = ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.EVERYONE )
                           .user( User.create().key( PrincipalKey.ofAnonymous() ).login( "anonymous" ).build() )
                           .build() )
            .build()
            .callWith( () -> this.contentService.getNearestSite( childLevel2.getId() ) );

        assertNull( fetchedSite );
    }

    private Content createSite()
    {
        return this.contentService.create( CreateContentParams.create()
                                               .type( ContentTypeName.site() )
                                               .displayName( "My mock site" )
                                               .contentData( new PropertyTree() )
                                               .parent( ContentPath.ROOT )
                                               .build() );
    }
}
