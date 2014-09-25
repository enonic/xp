package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static junit.framework.Assert.assertEquals;

public class GetRootContentTypesCommandTest
{
    private GetRootContentTypesCommand command;

    private ContentTypeDao contentTypeDao;

    private MixinService mixinService;

    @Before
    public void setUp()
        throws Exception
    {
        this.mixinService = Mockito.mock( MixinService.class );
        this.contentTypeDao = Mockito.mock( ContentTypeDao.class );

        command = new GetRootContentTypesCommand().contentTypeDao( this.contentTypeDao ).mixinService( this.mixinService );
    }

    @Test
    public void getRootContentTypes()
        throws Exception
    {
        // setup
        final ContentType contentType1 = ContentType.
            newContentType().
            name( "mymodule:my_content_type1" ).
            displayName( "Display Name 1" ).
            superType( null ).
            setBuiltIn().
            build();

        final ContentType contentType2 = ContentType.
            newContentType().
            name( "mymodule:my_content_type2" ).
            displayName( "Display Name 2" ).
            superType( ContentTypeName.from( "mymodule:my_content_type2" ) ).
            build();

        final ContentTypes allContentTypes = ContentTypes.from( contentType1, contentType2 );
        Mockito.when( contentTypeDao.getAllContentTypes() ).thenReturn( allContentTypes );

        // exercise
        final ContentTypes contentTypes = this.command.execute();

        // verify
        assertEquals( 1, contentTypes.getSize() );
        assertEquals( "mymodule:my_content_type1", contentTypes.get( 0 ).getName().toString() );
    }
}
