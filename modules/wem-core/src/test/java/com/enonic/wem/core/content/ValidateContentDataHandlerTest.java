package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static org.junit.Assert.*;

public class ValidateContentDataHandlerTest
    extends AbstractCommandHandlerTest
{
    private ValidateContentDataHandler handler;

    private ContentTypeDao contentTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        contentTypeDao = Mockito.mock( ContentTypeDao.class );
        handler = new ValidateContentDataHandler();
        handler.setContext( this.context );
        handler.setContentTypeDao( contentTypeDao );
    }

    @Test
    public void validation_with_errors()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            name( "my_type" ).
            addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).addFormItem(
                newFormItemSet().name( "mySet" ).required( true ).addFormItem(
                    newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build() ).
            build();

        Mockito.when( contentTypeDao.select( Mockito.any( ContentTypeNames.class ), Mockito.any( Session.class ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        final Content content = newContent().type( contentType.getQualifiedName() ).build();

        // exercise
        final ValidateContentData command =
            Commands.content().validate().contentData( content.getContentData() ).contentType( contentType.getQualifiedName() );
        this.handler.setCommand( command );
        this.handler.handle();

        // test
        final DataValidationErrors result = command.getResult();
        assertTrue( result.hasErrors() );
        assertEquals( 1, result.size() );
    }

    @Test
    public void validation_no_errors()
        throws Exception
    {
        // setup
        final FieldSet fieldSet = newFieldSet().label( "My layout" ).name( "myLayout" ).addFormItem(
            newFormItemSet().name( "mySet" ).required( true ).addFormItem(
                newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();
        final ContentType contentType = ContentType.newContentType().
            name( "my_type" ).
            addFormItem( fieldSet ).
            build();

        Mockito.when( contentTypeDao.select( Mockito.any( ContentTypeNames.class ), Mockito.any( Session.class ) ) ).thenReturn(
            ContentTypes.from( contentType ) );

        final Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.getContentData().setProperty( "mySet.myInput", new Value.String( "thing" ) );

        // exercise
        final ValidateContentData command =
            Commands.content().validate().contentData( content.getContentData() ).contentType( contentType.getQualifiedName() );
        this.handler.setCommand( command );
        this.handler.handle();

        // test
        final DataValidationErrors result = command.getResult();
        assertFalse( result.hasErrors() );
        assertEquals( 0, result.size() );
    }

}
