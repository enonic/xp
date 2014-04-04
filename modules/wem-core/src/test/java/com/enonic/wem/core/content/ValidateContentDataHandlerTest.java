package com.enonic.wem.core.content;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.ValidateContentData;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.FieldSet;
import com.enonic.wem.api.form.FormItemSet;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static org.junit.Assert.*;


public class ValidateContentDataHandlerTest
    extends AbstractCommandHandlerTest
{
    private ValidateContentDataHandler handler;

    private ContentTypeService contentTypeService;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        handler = new ValidateContentDataHandler();
        handler.setContext( this.context );
        handler.setContentTypeService( this.contentTypeService );
    }

    @Test
    public void validation_with_errors()
        throws Exception
    {
        // setup
        final ContentType contentType = ContentType.newContentType().
            name( "my_type" ).
            addFormItem( FieldSet.newFieldSet().
                label( "My layout" ).
                name( "myLayout" ).
                addFormItem( FormItemSet.newFormItemSet().name( "mySet" ).required( true ).
                    addFormItem( Input.newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).
                        build() ).
                    build() ).
                build() ).
            build();

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = Content.newContent().path( "/mycontent" ).type( contentType.getName() ).build();

        // exercise
        final ValidateContentData command =
            Commands.content().validate().contentData( content.getContentData() ).contentType( contentType.getName() );
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

        Mockito.when( contentTypeService.getByName( Mockito.isA( GetContentTypeParams.class ) ) ).thenReturn( contentType );

        final Content content = newContent().path( "/mycontent" ).type( contentType.getName() ).build();
        content.getContentData().setProperty( "mySet.myInput", new Value.String( "thing" ) );

        // exercise
        final ValidateContentData command =
            Commands.content().validate().contentData( content.getContentData() ).contentType( contentType.getName() );
        this.handler.setCommand( command );
        this.handler.handle();

        // test
        final DataValidationErrors result = command.getResult();
        assertFalse( result.hasErrors() );
        assertEquals( 0, result.size() );

    }

}
