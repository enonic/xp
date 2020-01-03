package com.enonic.xp.core.content;

import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_duplicate
    extends AbstractContentServiceTest
{

    @Test
    public void root_content()
        throws Exception
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        final Content duplicatedContent = doDuplicateContent( rootContent );

        assertNotNull( duplicatedContent );
        assertEquals( rootContent.getDisplayName(), duplicatedContent.getDisplayName() );
        assertEquals( rootContent.getParentPath(), duplicatedContent.getParentPath() );
        assertEquals( rootContent.getPath().toString() + "-copy", duplicatedContent.getPath().toString() );
    }

    @Test
    public void deep_children()
        throws Exception
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
    public void skip_children()
        throws Exception
    {
        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childrenLevel1 = createContent( rootContent.getPath() );
        final Content childrenLevel2 = createContent( childrenLevel1.getPath() );

        final Content duplicatedContent = doDuplicateContent( rootContent, false );

        assertFalse( duplicatedContent.hasChildren() );
    }

    @Test
    public void some_metadata_reset_on_duplicate()
        throws Exception
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

    private Content doDuplicateContent( final Content content )
    {
        return this.doDuplicateContent( content, true );
    }

    private Content doDuplicateContent( final Content content, final Boolean includeChildren )
    {
        final DuplicateContentParams params =
            DuplicateContentParams.create().contentId( content.getId() ).includeChildren( includeChildren ).build();
        final DuplicateContentsResult result = contentService.duplicate( params );

        return this.contentService.getById( result.getDuplicatedContents().first() );
    }

    @Test
    public void audit_data()
        throws Exception
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final Content rootContent = createContent( ContentPath.ROOT );
        final Content childContent = createContent( rootContent.getPath() );

        final Content duplicatedContent = doDuplicateContent( rootContent );

        Mockito.verify( auditLogService, Mockito.timeout( 5000 ).times( 3 ) ).log( captor.capture() );

        final PropertySet logResultSet = captor.getValue().getData().getSet( "result" );

        final Iterable<String> ids = logResultSet.getStrings( "duplicatedContents" );

        assertEquals( 2, StreamSupport.stream( ids.spliterator(), false ).count() );
        assertTrue( StreamSupport.stream( ids.spliterator(), false ).anyMatch( id -> id.equals( duplicatedContent.getId().toString() ) ) );
    }
}
