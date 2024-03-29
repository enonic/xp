package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ContentServiceImplTest_applyPermissions
    extends AbstractContentServiceTest
{

    @Test
    public void content_not_found()
        throws Exception
    {
        final ApplyContentPermissionsParams applyParams = ApplyContentPermissionsParams.create().
            contentId( ContentId.from( "id1" ) ).
            applyContentPermissionsListener( mock( ApplyPermissionsListener.class ) ).
            build();

        final ApplyContentPermissionsResult result = this.contentService.applyPermissions( applyParams );

        assertEquals( result.getSkippedContents().getSize(), 0 );
        assertEquals( result.getSucceedContents().getSize(), 0 );
    }

    @Test
    public void success()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );
        final Content child =
            this.contentService.create( CreateContentParams.create( createContentParams ).parent( content.getPath() ).build() );

        final ApplyContentPermissionsParams applyParams = ApplyContentPermissionsParams.create().
            contentId( content.getId() ).
            applyContentPermissionsListener( mock( ApplyPermissionsListener.class ) ).
            build();

        final ApplyContentPermissionsResult result = this.contentService.applyPermissions( applyParams );

        assertEquals( result.getSkippedContents().getSize(), 0 );
        assertEquals( result.getSucceedContents().getSize(), 2 );
        assertTrue( result.getSucceedContents().contains( child.getPath() ) );
        assertTrue( result.getSucceedContents().contains( content.getPath() ) );
    }

    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final ApplyContentPermissionsParams applyParams = ApplyContentPermissionsParams.create().
            contentId( content.getId() ).
            applyContentPermissionsListener( mock( ApplyPermissionsListener.class ) ).
            build();

        Mockito.reset( auditLogService );

        this.contentService.applyPermissions( applyParams );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );
        final LogAuditLogParams log = captor.getValue();

        assertThat( log.getType() ).isEqualTo( "system.content.applyPermissions" );
        assertThat( log.getData().getString( "result.succeedContents" ) ).isEqualTo( content.getPath().toString() );
    }
}
