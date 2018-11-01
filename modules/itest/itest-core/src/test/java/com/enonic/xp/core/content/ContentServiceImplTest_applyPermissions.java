package com.enonic.xp.core.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_applyPermissions
    extends AbstractContentServiceTest
{

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void content_not_found()
        throws Exception
    {
        final ApplyContentPermissionsParams applyParams = ApplyContentPermissionsParams.create().
            contentId( ContentId.from( "id1" ) ).
            applyContentPermissionsListener( Mockito.mock( ApplyPermissionsListener.class ) ).
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
            applyContentPermissionsListener( Mockito.mock( ApplyPermissionsListener.class ) ).
            build();

        final ApplyContentPermissionsResult result = this.contentService.applyPermissions( applyParams );

        assertEquals( result.getSkippedContents().getSize(), 0 );
        assertEquals( result.getSucceedContents().getSize(), 1 );
        assertTrue( result.getSucceedContents().contains( child.getId() ) );
    }

}
