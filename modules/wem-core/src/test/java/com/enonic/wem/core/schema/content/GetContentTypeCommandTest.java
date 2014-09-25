package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static org.junit.Assert.*;

public class GetContentTypeCommandTest
{
    private GetContentTypeCommand command;

    private ContentTypeDao contentTypeDao;

    private MixinService mixinService;

    @Before
    public void setUp()
        throws Exception
    {
        this.mixinService = Mockito.mock( MixinService.class );
        this.contentTypeDao = Mockito.mock( ContentTypeDao.class );

        command = new GetContentTypeCommand().contentTypeDao( this.contentTypeDao ).mixinService( this.mixinService );
    }

    @Test
    public void handle()
        throws Exception
    {
        final String name = "mymodule:content_type_1";
        final String displayName = "DisplayName";
        final String description = "Description";

        final ContentTypeName contentTypeName = ContentTypeName.from( name );

        final ContentType contentTypeBuilder =
            ContentType.newContentType().superType( ContentTypeName.structured() ).name( contentTypeName ).displayName(
                displayName ).description( description ).build();
        Mockito.when( contentTypeDao.getContentType( Mockito.eq( contentTypeName ) ) ).thenReturn( contentTypeBuilder );

        final ContentTypes allContentTypes = ContentTypes.from( createContentType( name, displayName, description ) );
        Mockito.when( contentTypeDao.getAllContentTypes() ).thenReturn( allContentTypes );

        // Exercise:
        GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentTypeName );
        final ContentType contentType = command.params( params ).execute();

        // Verify
        assertEquals( displayName, contentType.getDisplayName() );
        assertEquals( description, contentType.getDescription() );
    }

    private ContentType createContentType( final String name, final String displayName, final String description )
    {
        return ContentType.newContentType().superType( ContentTypeName.structured() ).displayName( displayName ).name( name ).description(
            description ).build();
    }
}
