package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static junit.framework.Assert.assertEquals;

public class GetChildContentTypesCommandTest
    extends AbstractCommandHandlerTest
{
    private GetChildContentTypesCommand command;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        final MixinService mixinService = Mockito.mock( MixinService.class );
        this.contentTypeDao = Mockito.mock( ContentTypeDao.class );

        command = new GetChildContentTypesCommand().contentTypeDao( this.contentTypeDao ).mixinService( mixinService );
    }

    @Test
    public void getChildContentTypes()
        throws Exception
    {
        // setup
        // setup
        final ContentType contentType1 = ContentType.
            newContentType().
            name( "my_content_type1" ).
            displayName( ContentTypeName.unstructured().toString() ).
            superType( null ).
            builtIn( true ).
            build();

        final ContentType contentType2 = ContentType.
            newContentType().
            name( "my_content_type2" ).
            displayName( "Display Name 2" ).
            superType( ContentTypeName.from( "my_content_type1" ) ).
            build();

        final ContentType contentType3 = ContentType.
            newContentType().
            name( "my_content_type3" ).
            displayName( "Display Name 3" ).
            superType( ContentTypeName.from( "my_content_type2" ) ).
            build();

        final ContentType contentType4 = ContentType.
            newContentType().
            name( "my_content_type4" ).
            displayName( "Display Name 4" ).
            superType( ContentTypeName.from( "my_content_type2" ) ).
            build();

        final ContentType contentType5 = ContentType.
            newContentType().
            name( ContentTypeName.folder().toString() ).
            displayName( "Folder root content type" ).
            builtIn( true ).
            build();

        final ContentTypes allContentTypes = ContentTypes.from( contentType1, contentType2, contentType3, contentType4, contentType5 );
        Mockito.when( contentTypeDao.getAllContentTypes() ).thenReturn( allContentTypes );


        // exercise
        GetChildContentTypesParams params = new GetChildContentTypesParams().parentName( contentType5.getName() );
        ContentTypes types = this.command.params( params ).execute();

        // verify
        assertEquals( 0, types.getSize() );

        // exercise
        params = new GetChildContentTypesParams().parentName( contentType1.getName() );
        types = this.command.params( params ).execute();

        // verify
        assertEquals( 1, types.getSize() );
        assertEquals( "my_content_type2", types.get( 0 ).getName().toString() );

        // exercise
        params = new GetChildContentTypesParams().parentName( contentType2.getName() );
        types = this.command.params( params ).execute();

        // verify
        assertEquals( 2, types.getSize() );
        assertEquals( "my_content_type3", types.get( 0 ).getName().toString() );
        assertEquals( "my_content_type4", types.get( 1 ).getName().toString() );
    }
}
