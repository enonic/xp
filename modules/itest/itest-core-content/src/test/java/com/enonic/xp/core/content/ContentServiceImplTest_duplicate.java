package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DuplicateContentListener;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

class ContentServiceImplTest_duplicate
    extends AbstractContentServiceTest
{

    @Test
    void root_content()
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        final Content duplicatedContent = doDuplicateContent( rootContent );

        assertNotNull( duplicatedContent );
        assertEquals( rootContent.getDisplayName(), duplicatedContent.getDisplayName() );
        assertEquals( rootContent.getParentPath(), duplicatedContent.getParentPath() );
        assertEquals( rootContent.getPath().toString() + "-copy", duplicatedContent.getPath().toString() );
    }

    @Test
    void deep_children()
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childrenLevel1 = createContent( rootContent.getPath() );
        final Content childrenLevel2 = createContent( childrenLevel1.getPath() );
        final Content duplicatedContent = doDuplicateContent( childrenLevel2 );

        assertNotNull( duplicatedContent );
        assertEquals( childrenLevel2.getDisplayName(), duplicatedContent.getDisplayName() );
        assertEquals( childrenLevel2.getParentPath(), duplicatedContent.getParentPath() );
        assertEquals( childrenLevel2.getPath().toString() + "-copy", duplicatedContent.getPath().toString() );
    }

    @Test
    void skip_children()
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childrenLevel1 = createContent( rootContent.getPath() );
        final Content childrenLevel2 = createContent( childrenLevel1.getPath() );

        final DuplicateContentParams params =
            DuplicateContentParams.create().contentId( rootContent.getId() ).includeChildren( false ).build();
        final DuplicateContentsResult result = contentService.duplicate( params );

        final Content duplicatedContent = this.contentService.getById( result.getDuplicatedContents().first() );

        assertEquals( 0, this.contentService.findIdsByParent(
            FindContentByParentParams.create().parentId( duplicatedContent.getId() ).build() ).getContentIds().getSize() );
    }

    @Test
    void some_metadata_reset_on_duplicate()
    {
        final User otherUser = User.create().
            key( PrincipalKey.ofUser( IdProviderKey.system(), "fisk" ) ).
            login( "fisk" ).
            build();

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "rootContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( otherUser.getKey() ).
                    allowAll().
                    build() ).
                build() ).
            build();

        final Content rootContent = this.contentService.create( createContentParams );

        final Context duplicateContext = ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.create().
                user( otherUser ).
                principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
                build() ).
            build();

        final Content duplicateContent = duplicateContext.callWith( () -> doDuplicateContent( rootContent ) );

        assertTrue( rootContent.getModifiedTime().isBefore( duplicateContent.getModifiedTime() ) );
        assertTrue( rootContent.getCreatedTime().isBefore( duplicateContent.getCreatedTime() ) );
        assertEquals( otherUser.getKey(), duplicateContent.getModifier() );
        assertEquals( otherUser.getKey(), duplicateContent.getOwner() );
        assertEquals( otherUser.getKey(), duplicateContent.getCreator() );
    }

    @Test
    void data_removed_on_duplicate()
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "rootContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            permissions( AccessControlList.create().
                build() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        this.nodeService.patch( PatchNodeParams.create().
            id( NodeId.from( content.getId() ) ).
            editor( toBeEdited -> {
                toBeEdited.data.addSet( ContentPropertyNames.PUBLISH_INFO, toBeEdited.data.newSet() );
                toBeEdited.data.addString( ContentPropertyNames.ORIGIN_PROJECT, "some-project" );
                toBeEdited.data.addStrings( ContentPropertyNames.INHERIT, ContentInheritType.CONTENT.name(),
                                            ContentInheritType.NAME.name() );
            } ).
            build() );

        final Content duplicateContent = doDuplicateContent( content );

        assertNull( duplicateContent.getPublishInfo() );
        assertNull( duplicateContent.getOriginProject() );
        assertTrue( duplicateContent.getInherit().isEmpty() );
    }

    @Test
    void workflow_info_changed()
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "rootContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            permissions( AccessControlList.create().
            build() ).
            workflowInfo( WorkflowInfo.ready() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final DuplicateContentParams params = DuplicateContentParams.create()
            .contentId( content.getId() )
            .workflowInfo( WorkflowInfo.inProgress() )
            .includeChildren( false )
            .build();
        final DuplicateContentsResult result = contentService.duplicate( params );

        final Content duplicatedContent = this.contentService.getById( result.getDuplicatedContents().first() );

        assertEquals( WorkflowState.IN_PROGRESS, duplicatedContent.getWorkflowInfo().getState() );
        assertEquals( "rootcontent-copy", duplicatedContent.getName().toString() );
    }

    @Test
    void duplicate_with_listener()
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        createContent( rootContent.getPath() );

        final TestListener listener = new TestListener();

        contentService.duplicate( DuplicateContentParams.create()
                                      .contentId( rootContent.getId() )
                                      .includeChildren( true )
                                      .duplicateContentListener( listener )
                                      .build() );

        assertEquals( 2, listener.duplicated );
    }

    @Test
    void referred_contents_duplicated_when_children_excluded()
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        final Content templates = createContent( content.getPath(), "templates" );
        final Content template = createContent( templates.getPath(), "template" );
        createContent( content.getPath(), "article" );

        addReference( content, template );

        refresh();

        final DuplicateContentsResult result =
            contentService.duplicate( DuplicateContentParams.create().contentId( content.getId() ).includeChildren( false ).build() );

        assertEquals( 3, result.getDuplicatedContents().getSize() );

        final Content duplicatedContent = this.contentService.getById( result.getDuplicatedContents().first() );

        final Content duplicatedTemplates = getSingleChild( duplicatedContent );
        assertEquals( templates.getDisplayName(), duplicatedTemplates.getDisplayName() );

        final Content duplicatedTemplate = getSingleChild( duplicatedTemplates );
        assertEquals( template.getDisplayName(), duplicatedTemplate.getDisplayName() );

        assertEquals( new Reference( NodeId.from( duplicatedTemplate.getId() ) ),
                      duplicatedContent.getData().getReference( "myRef" ) );
    }

    @Test
    void referred_content_outside_tree_not_duplicated()
    {
        final Content content = createContent( ContentPath.ROOT, "content" );
        final Content other = createContent( ContentPath.ROOT, "other" );
        createContent( content.getPath(), "article" );

        addReference( content, other );

        refresh();

        final DuplicateContentsResult result =
            contentService.duplicate( DuplicateContentParams.create().contentId( content.getId() ).includeChildren( false ).build() );

        assertEquals( 1, result.getDuplicatedContents().getSize() );

        final Content duplicatedContent = this.contentService.getById( result.getDuplicatedContents().first() );

        assertEquals( 0, this.contentService.findIdsByParent(
            FindContentByParentParams.create().parentId( duplicatedContent.getId() ).build() ).getContentIds().getSize() );

        assertEquals( new Reference( NodeId.from( other.getId() ) ), duplicatedContent.getData().getReference( "myRef" ) );
    }

    private void addReference( final Content content, final Content referred )
    {
        final PropertyTree data = content.getData().copy();
        data.addReference( "myRef", new Reference( NodeId.from( referred.getId() ) ) );

        this.contentService.update( new UpdateContentParams().contentId( content.getId() ).editor( edit -> edit.data = data ) );
    }

    private Content getSingleChild( final Content content )
    {
        final ContentIds children = this.contentService.findIdsByParent(
            FindContentByParentParams.create().parentId( content.getId() ).build() ).getContentIds();

        assertEquals( 1, children.getSize() );

        return this.contentService.getById( children.first() );
    }

    private Content doDuplicateContent( final Content content )
    {
        final DuplicateContentParams params =
            DuplicateContentParams.create().contentId( content.getId() ).includeChildren( true ).build();
        final DuplicateContentsResult result = contentService.duplicate( params );

        return this.contentService.getById( result.getDuplicatedContents().first() );
    }

    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childContent = createContent( rootContent.getPath() );

        Mockito.reset( auditLogService );

        final Content duplicatedContent = doDuplicateContent( rootContent );

        verify( auditLogService, atMost(2) ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.duplicate" ) ;

        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getStrings( "duplicatedContents" ), LIST )
            .hasSize( 2 );
    }

    @Test
    void testCreateVariant()
    {
        final CreateContentParams myContent = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "My Content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .permissions( AccessControlList.create().build() )
            .build();

        final Content content = this.contentService.create( myContent );

        DuplicateContentParams duplicateParams = DuplicateContentParams.create()
            .contentId( content.getId() )
            .includeChildren( false )
            .variant( true )
            .name( "variantName" )
            .parent( content.getPath() )
            .build();

        DuplicateContentsResult result = contentService.duplicate( duplicateParams );

        Content duplicatedContent = this.contentService.getById( result.getDuplicatedContents().first() );

        assertEquals( content.getId(), duplicatedContent.getVariantOf() );
        assertEquals( duplicateParams.getName(), duplicatedContent.getName().toString() );
        assertEquals( duplicateParams.getParent(), duplicatedContent.getParentPath() );

        Content updatedContent = this.contentService.update(
            new UpdateContentParams().contentId( duplicatedContent.getId() ).editor( edit -> edit.displayName = "Display Name" ) );

        assertEquals( duplicatedContent.getVariantOf(), updatedContent.getVariantOf() );
        assertEquals( "Display Name", updatedContent.getDisplayName() );

        // try to create a variant for content which already is a variant, in result that must be a duplicated content

        duplicateParams = DuplicateContentParams.create()
            .contentId( duplicatedContent.getId() )
            .includeChildren( false )
            .variant( true )
            .name( "variantName-new" )
            .parent( content.getPath() )
            .build();

        result = contentService.duplicate( duplicateParams );

        Content duplicatedContent2 = this.contentService.getById( result.getDuplicatedContents().first() );

        assertEquals( content.getId(), duplicatedContent2.getVariantOf() );
        assertEquals( duplicateParams.getName(), duplicatedContent2.getName().toString() );
        assertEquals( duplicateParams.getParent(), duplicatedContent2.getParentPath() );
    }


    private static final class TestListener
        implements DuplicateContentListener
    {
        int duplicated;

        @Override
        public void contentDuplicated( final int count )
        {
            duplicated += count;
        }

        @Override
        public void contentReferencesUpdated( final int count )
        {
        }
    }
}
