package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.schema.content.GetContentTypesParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static org.junit.Assert.*;

public class GetContentTypesCommandTest
    extends AbstractCommandHandlerTest
{
    private GetContentTypesCommand command;

    private ContentTypeDao contentTypeDao;

    private MixinService mixinService;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        this.mixinService = Mockito.mock( MixinService.class );
        this.contentTypeDao = Mockito.mock( ContentTypeDao.class );

        command = new GetContentTypesCommand().
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService );
    }

    @Test
    public void handle()
        throws Exception
    {
        final ContentTypes allContentTypes = ContentTypes.from(
            createContentType( "content_type_1", "DisplayName", "Description" ),
            createContentType( "content_type_2", "DisplayName2", "Description2" ) );
        Mockito.when( contentTypeDao.getAllContentTypes() ).thenReturn( allContentTypes );

        final ContentType.Builder contentTypeBuilder1 = ContentType.
            newContentType().
            displayName( "DisplayName" ).
            description( "Description" ).
            name( "content_type_1" ) ;
        Mockito.when( contentTypeDao.getContentType( Mockito.eq( ContentTypeName.from( "content_type_1" ) ) ) ).thenReturn( contentTypeBuilder1 );

        final ContentType.Builder contentTypeBuilder2 = ContentType.
            newContentType().
            displayName( "DisplayName2" ).
            description( "Description2" ).
            name( "content_type_2" ) ;
        Mockito.when( contentTypeDao.getContentType( Mockito.eq( ContentTypeName.from( "content_type_2" ) ) ) ).thenReturn( contentTypeBuilder2 );

        // Exercise:
        final GetContentTypesParams params = new GetContentTypesParams().contentTypeNames( ContentTypeNames.from( "content_type_1", "content_type_2" ) );
        final ContentTypes result = this.command.params( params ).execute();

        // Verify
        assertEquals( 2, result.getSize() );
        verifyContentType( "content_type_1", "DisplayName", "Description", result );
        verifyContentType( "content_type_2", "DisplayName2", "Description2", result );
    }

    private void verifyContentType( final String contentTypeName, final String displayName, final String description, final ContentTypes result )
    {
        final ContentType contentType = result.getContentType( ContentTypeName.from( contentTypeName ) );
        assertNotNull( contentType );
        assertEquals( contentTypeName, contentType.getName().toString() );
        assertEquals( displayName, contentType.getDisplayName() );
        assertEquals( description, contentType.getDescription() );
    }

    private ContentType createContentType( final String name, final String displayName, final String description )
    {
        return ContentType.newContentType().displayName( displayName ).name( name ).description( description ).build();
    }
}
