package com.enonic.wem.core.content;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPropertyNames;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.CreateMediaParams;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.site.ModuleConfigs;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.acl.AccessControlList;

import static org.junit.Assert.*;

public class ContentServiceImplTest_create
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void create_content_generated_properties()
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

        final Content storedContent = this.contentService.getById( content.getId() );

        assertNotNull( storedContent.getName() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getModifiedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getChildOrder() );
        assertEquals( ContentConstants.DEFAULT_CHILD_ORDER, storedContent.getChildOrder() );
    }

    @Test
    public void create_with_attachments()
        throws Exception
    {
        final String name = "cat-small.jpg";
        final ByteSource image = loadImage( name );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            type( ContentTypeName.imageMedia() ).
            createAttachments( createAttachment( "cat", "image/jpeg", image ) ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 4, attachments.getSize() ); // original, small, medium, large
    }

    @Test
    @Ignore(value = "This fails on my maching (srs). Need to investigate.")
    public void create_media_image()
        throws Exception
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams();
        createMediaParams.byteSource( loadImage( "cat-small.jpg" ) ).
            name( "Small cat" ).
            parent( ContentPath.ROOT );

        final Content content = this.contentService.create( createMediaParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertNotNull( storedContent.getName() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getModifiedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getData().getString( ContentPropertyNames.MEDIA ) );
        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 4, attachments.getSize() ); // original, small, medium, large
    }

    @Test
    public void create_site()
        throws Exception
    {

        final CreateSiteParams createSiteParams = new CreateSiteParams();
        createSiteParams.parent( ContentPath.ROOT ).
            displayName( "My site" ).
            description( "This is my site" ).
            moduleConfigs( ModuleConfigs.empty() );

        final Content content = this.contentService.create( createSiteParams );

        assertNotNull( content.getName() );
        assertNotNull( content.getCreatedTime() );
        assertNotNull( content.getCreator() );
        assertNotNull( content.getModifiedTime() );
        assertNotNull( content.getModifier() );
    }


}
