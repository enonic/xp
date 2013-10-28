package com.enonic.wem.core.schema.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.UpdateContentType;
import com.enonic.wem.api.command.schema.content.UpdateContentTypeResult;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;
import com.enonic.wem.api.schema.content.validator.InvalidContentTypeException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.editor.SetContentTypeEditor.newSetContentTypeEditor;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class UpdateContentTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateContentTypeHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new UpdateContentTypeHandler();
        handler.setContext( this.context );
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void updateContentType()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeDao.select( Mockito.eq( ContentTypeNames.from( ContentTypeName.structured() ) ),
                                             Mockito.any( Session.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.STRUCTURED ) );

        ContentType existingContentType = newContentType().
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();

        ContentTypes contentTypes = ContentTypes.from( existingContentType );
        Mockito.when( contentTypeDao.select( isA( ContentTypeName.class ), any( Session.class ) ) ).thenReturn(
            existingContentType );

        UpdateContentType command =
            Commands.contentType().update().qualifiedName( ContentTypeName.from( "my_content_type" ) );
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( "Changed" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();
        command.editor( editor );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( contentTypeDao, atLeastOnce() ).update( Mockito.isA( ContentType.class ), Mockito.any( Session.class ) );
        assertEquals( UpdateContentTypeResult.SUCCESS, command.getResult() );
    }


    @Test(expected = InvalidContentTypeException.class)
    public void given_superType_that_is_final_when_handle_then_InvalidContentTypeException()
        throws Exception
    {
        // setup
        Mockito.when( contentTypeDao.select( Mockito.eq( ContentTypeNames.from( ContentTypeName.shortcut() ) ),
                                             Mockito.any( Session.class ) ) ).thenReturn(
            ContentTypes.from( ContentTypesInitializer.SHORTCUT ) );

        ContentType existingContentType = newContentType().
            name( "my_content_type" ).
            displayName( "My content type" ).
            setAbstract( false ).
            superType( ContentTypeName.structured() ).
            build();

        Mockito.when(
            contentTypeDao.select( eq( ContentTypeName.from( "my_content_type" ) ), any( Session.class ) ) ).thenReturn(
            existingContentType );

        UpdateContentType command =
            Commands.contentType().update().qualifiedName( ContentTypeName.from( "my_content_type" ) );
        final ContentTypeEditor editor = newSetContentTypeEditor().
            displayName( "Changed" ).
            setAbstract( false ).
            superType( ContentTypeName.shortcut() ).
            build();
        command.editor( editor );

        // exercise
        this.handler.setCommand( command );
        this.handler.handle();
    }

}
