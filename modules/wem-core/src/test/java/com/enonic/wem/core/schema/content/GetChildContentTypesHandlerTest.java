package com.enonic.wem.core.schema.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetAllContentTypes;
import com.enonic.wem.api.command.schema.content.GetChildContentTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static junit.framework.Assert.assertEquals;

public class GetChildContentTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetChildContentTypesHandler handler;

    @Before
    public void setUp()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();
        handler = new GetChildContentTypesHandler();
        handler.setContext( this.context );
    }

    @Test
    public void getChildContentTypes()
        throws Exception
    {
        // setup
        final ContentType contentType1 = newContentType().
            name( ContentTypeName.unstructured() ).
            builtIn( true ).
            displayName( "Unstructured root content type" ).
            build();
        final ContentType contentType2 = newContentType().
            name( "my_type" ).
            displayName( "My content type" ).
            superType( contentType1.getContentTypeName() ).
            build();
        final ContentType contentType3 = newContentType().
            name( "sub_type_1" ).
            displayName( "My sub-content-1 type" ).
            superType( contentType2.getContentTypeName() ).
            build();
        final ContentType contentType4 = newContentType().
            name( "sub_type_2" ).
            displayName( "My sub-content-2 type" ).
            superType( contentType2.getContentTypeName() ).
            build();
        final ContentType contentType5 = newContentType().
            name( ContentTypeName.folder() ).
            builtIn( true ).
            displayName( "Folder root content type" ).
            build();

        final ContentTypes contentTypes = ContentTypes.from( contentType1, contentType2, contentType3, contentType4, contentType5 );

        Mockito.when( client.execute( Mockito.isA( GetAllContentTypes.class ) ) ).thenReturn( contentTypes );

        // exercise
        GetChildContentTypes command = Commands.contentType().get().children().parentName( contentType5.getContentTypeName() );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        ContentTypes types = command.getResult();
        assertEquals( 0, types.getSize() );

        // exercise
        command = Commands.contentType().get().children().parentName( contentType1.getContentTypeName() );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        types = command.getResult();
        assertEquals( 1, types.getSize() );
        assertEquals( "my_type", types.get( 0 ).getContentTypeName().toString() );

        // exercise
        command = Commands.contentType().get().children().parentName( contentType2.getContentTypeName() );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        types = command.getResult();
        assertEquals( 2, types.getSize() );
        assertEquals( "sub_type_1", types.get( 0 ).getContentTypeName().toString() );
        assertEquals( "sub_type_2", types.get( 1 ).getContentTypeName().toString() );
    }
}
