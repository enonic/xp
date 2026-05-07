package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyExistsException;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.MixinMapping;
import com.enonic.xp.site.MixinMappings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContentServiceImplTest_move
    extends AbstractContentServiceTest
{

    @Test
    void move_to_folder_starting_with_same_name()
    {

        final Content site = createContent( ContentPath.ROOT, "site" );
        final Content child1 = createContent( site.getPath(), "child1" );
        createContent( child1.getPath(), "child1_1" );
        createContent( child1.getPath(), "child2_1" );
        final Content site2 = createContent( ContentPath.ROOT, "site2" );

        final MoveContentParams params = MoveContentParams.create().
            contentId( child1.getId() ).
            parentContentPath( site2.getPath() ).
            build();
        final MoveContentsResult result = this.contentService.move( params );

        final Content movedContent = contentService.getById( result.getMovedContents().first() );

        assertEquals( 1, result.getMovedContents().getSize() );
        assertEquals( movedContent.getParentPath(), site2.getPath() );

    }

    @Test
    void move_from_site_to_root()
    {
        final PropertyTree siteData = new PropertyTree();
        siteData.setSet( "siteConfig", this.createSiteConfig( siteData ) );
        final Content site = createContent( ContentPath.ROOT, "site", siteData, ContentTypeName.site() );

        final Content content = createContent( site.getPath(), "child", new PropertyTree(), this.createMixins() );

        final MoveContentParams params =
            MoveContentParams.create().contentId( content.getId() ).parentContentPath( ContentPath.ROOT ).build();

        final MoveContentsResult result = this.contentService.move( params );

        final Content movedContent = contentService.getById( result.getMovedContents().first() );

        assertEquals( 1, movedContent.getMixins().getSize() );
    }

    @Test
    void move_already_exists()
    {
        final Content content = createContent( ContentPath.ROOT, "child", new PropertyTree(), ContentTypeName.site() );
        createContent( ContentPath.ROOT, "child-2", new PropertyTree(), ContentTypeName.site() );

        final Content content3 = createContent( content.getPath(), "child-2", new PropertyTree() );

        final MoveContentParams params =
            MoveContentParams.create().contentId( content3.getId() ).parentContentPath( content.getParentPath() ).build();

        assertThrows( ContentAlreadyExistsException.class, () -> this.contentService.move( params ) );
    }

    @Test
    void publish_time_reset_on_move()
    {
        final Content content = createContent( ContentPath.ROOT, "child" );
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        final ContentPublishInfo publishInfoBeforeMove = this.contentService.getById( content.getId() ).getPublishInfo();
        assertNotNull( publishInfoBeforeMove );
        assertNotNull( publishInfoBeforeMove.time() );

        final Content folder = createContent( ContentPath.ROOT, "folder" );

        this.contentService.move(
            MoveContentParams.create().contentId( content.getId() ).parentContentPath( folder.getPath() ).build() );

        final ContentPublishInfo publishInfoAfterMove = this.contentService.getById( content.getId() ).getPublishInfo();
        assertNotNull( publishInfoAfterMove );
        assertNull( publishInfoAfterMove.time() );
        assertNotNull( publishInfoAfterMove.from() );
        assertNotNull( publishInfoAfterMove.first() );
    }

    @Test
    void publish_time_reset_on_rename()
    {
        final Content content = createContent( ContentPath.ROOT, "child" );
        this.contentService.publish(
            PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).includeDependencies( false ).build() );

        final ContentPublishInfo publishInfoBeforeRename = this.contentService.getById( content.getId() ).getPublishInfo();
        assertNotNull( publishInfoBeforeRename );
        assertNotNull( publishInfoBeforeRename.time() );

        this.contentService.move(
            MoveContentParams.create().contentId( content.getId() ).newName( ContentName.from( "renamed-child" ) ).build() );

        final ContentPublishInfo publishInfoAfterRename = this.contentService.getById( content.getId() ).getPublishInfo();
        assertNotNull( publishInfoAfterRename );
        assertNull( publishInfoAfterRename.time() );
        assertNotNull( publishInfoAfterRename.from() );
        assertNotNull( publishInfoAfterRename.first() );
    }

    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final Content site = createContent( ContentPath.ROOT, "site" );
        final Content child1 = createContent( site.getPath(), "child1" );

        final MoveContentParams params = MoveContentParams.create().
            contentId( child1.getId() ).
            parentContentPath( ContentPath.ROOT ).
            build();

        Mockito.reset( auditLogService );

        this.contentService.move( params );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.move" ) ;
        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getString( "movedContents" ) )
            .isEqualTo( child1.getId().toString() );
    }

    private Mixins createMixins()
    {
        final MixinName mixinName = MixinName.from( "com.enonic.app.test:mixin1" );

        when( resourceService.processResource( isA( ResourceProcessor.class ) ) ).thenReturn( CmsDescriptor.create()
                                                                                                  .applicationKey( ApplicationKey.from(
                                                                                                      "com.enonic.app.test" ) )
                                                                                                  .mixinMappings( MixinMappings.from(
                                                                                                      MixinMapping.create()
                                                                                                          .mixinName( mixinName )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( false )
                                                                                                          .build() ) )
                                                                                                  .build() );

        final MixinDescriptor mixinDescriptor = MixinDescriptor.create().name( mixinName ).form( Form.create().build() ).build();
        when( mixinService.getByName( mixinDescriptor.getName() ) ).thenReturn( mixinDescriptor );

        return Mixins.create().add( new Mixin( mixinName, new PropertyTree() ) ).
            build();
    }

    private PropertySet createSiteConfig(PropertyTree tree)
    {
        PropertySet set = tree.newSet();
        set.addString( "applicationKey", "com.enonic.app.test" );
        set.addSet( "config" );
        return set;
    }
}
