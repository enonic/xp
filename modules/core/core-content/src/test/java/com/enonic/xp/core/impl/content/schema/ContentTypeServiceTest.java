package com.enonic.xp.core.impl.content.schema;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ContentTypeServiceTest
    extends ApplicationTestSupport
{
    protected ContentTypeServiceImpl service;

    protected MixinService mixinService;

    @Override
    protected void initialize()
        throws Exception
    {
        this.mixinService = Mockito.mock( MixinService.class );
        this.service = new ContentTypeServiceImpl( this.resourceService, this.applicationService, this.mixinService );
    }

    @Test
    public void testEmpty()
    {
        final ContentTypes types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 22, types1.getSize() );

        final ContentTypes types2 = this.service.getByApplication( ApplicationKey.from( "other" ) );
        assertNotNull( types2 );
        assertEquals( 0, types2.getSize() );

        final ContentType contentType = service.getByName( new GetContentTypeParams().contentTypeName( "other:mytype" ) );
        assertNull( contentType );
    }

    @Test
    public void testApplications()
        throws Exception
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );

        final ContentTypes types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 24, types1.getSize() );

        final ContentTypes types2 = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );
        assertNotNull( types2 );
        assertEquals( 1, types2.getSize() );

        final ContentTypes types3 = this.service.getByApplication( ApplicationKey.from( "myapp2" ) );
        assertNotNull( types3 );
        assertEquals( 1, types3.getSize() );

        final ContentType contentType = service.getByName( new GetContentTypeParams().contentTypeName( "myapp1:tag" ) );
        assertNotNull( contentType );
    }

    @Test
    public void testSystemApplication()
    {
        ContentTypes contentTypes = this.service.getAll();
        assertNotNull( contentTypes );
        assertEquals( 22, contentTypes.getSize() );

        ContentType contentType = service.getByName( new GetContentTypeParams().contentTypeName( ContentTypeName.folder() ) );
        assertNotNull( contentType );

        contentTypes = service.getByApplication( ApplicationKey.BASE );
        assertNotNull( contentTypes );
        assertEquals( 5, contentTypes.getSize() );

        contentTypes = service.getByApplication( ApplicationKey.PORTAL );
        assertNotNull( contentTypes );
        assertEquals( 4, contentTypes.getSize() );

        contentTypes = service.getByApplication( ApplicationKey.MEDIA_MOD );
        assertNotNull( contentTypes );
        assertEquals( 13, contentTypes.getSize() );

        contentType = service.getByName( new GetContentTypeParams().contentTypeName( ContentTypeName.site() ) );
        assertNotNull( contentType );
    }

    @Test
    public void getMimeTypes()
    {
        final Collection<String> audioMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.audioMedia() ) );
        assertEquals( 12, audioMimeTypes.size() );

        final Collection<String> imageMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.imageMedia() ) );
        assertEquals( 11, imageMimeTypes.size() );

        final Collection<String> videoMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.videoMedia() ) );
        assertEquals( 15, videoMimeTypes.size() );

        final Collection<String> archiveMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.archiveMedia() ) );
        assertEquals( 5, archiveMimeTypes.size() );

        final Collection<String> textMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.textMedia() ) );
        assertEquals( 3, textMimeTypes.size() );

        final Collection<String> codeMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.codeMedia() ) );
        assertEquals( 11, codeMimeTypes.size() );

        final Collection<String> dataMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.dataMedia() ) );
        assertEquals( 0, dataMimeTypes.size() );

        final Collection<String> documentMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.documentMedia() ) );
        assertEquals( 5, documentMimeTypes.size() );

        final Collection<String> execMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.executableMedia() ) );
        assertEquals( 14, execMimeTypes.size() );

        final Collection<String> presentationMimeTypes =
            this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.presentationMedia() ) );
        assertEquals( 4, presentationMimeTypes.size() );

        final Collection<String> spreadsheetMimeTypes =
            this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.spreadsheetMedia() ) );
        assertEquals( 2, spreadsheetMimeTypes.size() );

        final Collection<String> vectorMimeTypes = this.service.getMimeTypes( ContentTypeNames.from( ContentTypeName.vectorMedia() ) );
        assertEquals( 1, vectorMimeTypes.size() );
    }
}
