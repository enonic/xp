package com.enonic.wem.core.schema.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.schema.content.CreateContentType;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class CreateContentTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateContentTypeHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );

        handler = new CreateContentTypeHandler();
        handler.setContext( this.context );
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void createContentType()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeDao.select( Mockito.eq( ContentTypeNames.from( ContentTypeName.structured() ) ),
                                             Mockito.any( Session.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();

        CreateContentType command = contentType().create().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            icon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        Mockito.verify( contentTypeDao, Mockito.atLeastOnce() ).create( Mockito.isA( ContentType.class ), Mockito.any( Session.class ) );
        ContentTypeName contentTypeName = command.getResult();
        assertNotNull( contentTypeName );
        assertEquals( "my_content_type", contentTypeName.toString() );
    }

    @Test(expected = InvalidContentTypeException.class)
    public void given_superType_that_is_final_when_handle_then_InvalidContentTypeException()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeDao.select( Mockito.eq( ContentTypeNames.from( ContentTypeName.shortcut() ) ),
                                             Mockito.any( Session.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.SHORTCUT ) );

        ContentType contentType = newContentType().
            name( "my_content_type" ).
            displayName( "Inheriting a final ContentType" ).
            setAbstract( false ).
            superType( ContentTypeName.shortcut() ).
            build();

        // exercise
        final CreateContentType createCommand = contentType().create().
            name( contentType.getName() ).
            displayName( contentType.getDisplayName() ).
            superType( contentType.getSuperType() ).
            setAbstract( contentType.isAbstract() ).
            setFinal( contentType.isFinal() ).
            form( contentType.form() ).
            icon( contentType.getIcon() ).
            contentDisplayNameScript( contentType.getContentDisplayNameScript() );

        this.handler.setCommand( createCommand );
        this.handler.handle();
    }

}
