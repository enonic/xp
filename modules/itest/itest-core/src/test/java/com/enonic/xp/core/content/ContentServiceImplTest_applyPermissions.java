package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ContentServiceImplTest_applyPermissions
    extends AbstractContentServiceTest
{

    @Test
    public void content_not_found()
        throws Exception
    {
        final ApplyContentPermissionsParams applyParams = ApplyContentPermissionsParams.create()
            .contentId( ContentId.from( "id1" ) )
            .applyContentPermissionsListener( mock( ApplyPermissionsListener.class ) )
            .build();

        assertThrows( NodeNotFoundException.class, () -> this.contentService.applyPermissions( applyParams ) );
    }

    @Test
    public void success()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );
        final Content child =
            this.contentService.create( CreateContentParams.create( createContentParams ).parent( content.getPath() ).build() );

        final ApplyPermissionsListener listener = mock( ApplyPermissionsListener.class );

        final ApplyContentPermissionsParams applyParams = ApplyContentPermissionsParams.create()
            .contentId( content.getId() )
            .applyContentPermissionsListener( listener )
            .addPermissions( content.getPermissions() )
            .build();

        final ApplyContentPermissionsResult result = this.contentService.applyPermissions( applyParams );

        verify( listener, times( 1 ) ).permissionsApplied( 1 );

        assertEquals( 1, result.getResults().size() );

        assertEquals( content.getPermissions(),
                      result.getResult( content.getId(), ContextAccessor.current().getBranch() ).getPermissions() );
        assertNull( result.getResult( content.getId(), ContentConstants.BRANCH_MASTER ) );
    }

    @Test
    public void no_rights()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .inheritPermissions( false )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create()
                                        .principal( ContextAccessor.current().getAuthInfo().getUser().getKey() )
                                        .allowAll()
                                        .deny( Permission.WRITE_PERMISSIONS )
                                        .build() )
                              .build() )
            .build();

        final Content content = this.contentService.create( createContentParams );
        this.contentService.create( CreateContentParams.create( createContentParams ).parent( content.getPath() ).build() );

        final ApplyPermissionsListener listener = mock( ApplyPermissionsListener.class );

        final ApplyContentPermissionsParams applyParams = ApplyContentPermissionsParams.create()
            .contentId( content.getId() )
            .applyPermissionsScope( ApplyPermissionsScope.TREE )
            .applyContentPermissionsListener( listener )
            .build();

        final ApplyContentPermissionsResult result = this.contentService.applyPermissions( applyParams );

        verify( listener, times( 2 ) ).notEnoughRights( 1 );

        assertEquals( 2, result.getResults().size() );
        assertNull( result.getResult( content.getId(), ContextAccessor.current().getBranch() ) );
    }

    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        final ApplyContentPermissionsParams applyParams = ApplyContentPermissionsParams.create().contentId( content.getId() )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create()
                                        .principal( ContextAccessor.current().getAuthInfo().getUser().getKey() )
                                        .allowAll()
                                        .build() )
                              .build() )
            .applyContentPermissionsListener( mock( ApplyPermissionsListener.class ) )
            .build();

        Mockito.reset( auditLogService );

        this.contentService.applyPermissions( applyParams );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );
        final LogAuditLogParams log = captor.getValue();

        assertThat( log.getType() ).isEqualTo( "system.content.applyPermissions" );
        assertThat( log.getData().getString( "result." + content.getId() + ".master" ) ).isEqualTo(
            log.getData().getString( "result." + content.getId() + ".draft" ) );
    }
}
