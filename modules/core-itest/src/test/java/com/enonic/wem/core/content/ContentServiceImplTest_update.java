package com.enonic.wem.core.content;

import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.core.schema.content.BuiltinContentTypeProvider;

import static org.junit.Assert.*;

public class ContentServiceImplTest_update
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void update_content()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertEquals( "new display name", storedContent.getDisplayName() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getModifiedTime() );
    }

    @Test
    public void update_content_image()
        throws Exception
    {
        final ByteSource image = loadImage( "cat-small.jpg" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            type( ContentTypeName.imageMedia() ).
            createAttachments( createAttachment( "cat", "image/jpg", image ) ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                edit.displayName = "new display name";
            } ).
            createAttachments( createAttachment( "darth", "image/jpg", loadImage( "darth-small.jpg" ) ) );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 3, attachments.getSize() ); // original, small, medium
    }

    @Test
    public void update_content_data()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "testString", "value" );
        data.setString( "testString2", "value" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( data ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            type( BuiltinContentTypeProvider.FOLDER.getName() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final UpdateContentParams updateContentParams = new UpdateContentParams();
        updateContentParams.
            contentId( content.getId() ).
            editor( edit -> {
                final PropertyTree editData = edit.data;
                editData.setString( "testString", "value-updated" );
            } );

        this.contentService.update( updateContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertEquals( "This is my content", storedContent.getDisplayName() );
        assertEquals( "value-updated", storedContent.getData().getString( "testString" ) );
        assertEquals( "value", storedContent.getData().getString( "testString2" ) );
    }

}
