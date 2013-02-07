package com.enonic.wem.core.content;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.ValidateRootDataSet;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.form.FieldSet;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.content.type.validator.DataValidationErrors;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static com.enonic.wem.api.content.Content.newContent;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class ValidateContentDataHandlerTest
    extends AbstractCommandHandlerTest
{
    private ValidateContentDataHandler handlerValidate;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        handlerValidate = new ValidateContentDataHandler();
    }

    @Test
    public void validation_with_errors()
        throws Exception
    {
        // setup
        final ContentType contentType = newContentType().
            module( Module.SYSTEM.getName() ).
            name( "MyType" ).
            addFormItem( newFieldSet().label( "My layout" ).name( "myLayout" ).add( newFormItemSet().name( "mySet" ).required( true ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build() ).
            build();

        final Content content = newContent().type( contentType.getQualifiedName() ).build();

        // exercise
        final ValidateRootDataSet command =
            Commands.content().validate().rootDataSet( content.getRootDataSet() ).contentType( contentType );
        this.handlerValidate.handle( this.context, command );

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
        final FieldSet fieldSet = newFieldSet().label( "My layout" ).name( "myLayout" ).add(
            newFormItemSet().name( "mySet" ).required( true ).add(
                newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();
        final ContentType contentType = ContentType.newContentType().
            module( Module.SYSTEM.getName() ).
            name( "MyType" ).
            addFormItem( fieldSet ).
            build();

        final Content content = newContent().type( contentType.getQualifiedName() ).build();
        content.setData( "mySet.myInput", "thing" );

        // exercise
        final ValidateRootDataSet command =
            Commands.content().validate().rootDataSet( content.getRootDataSet() ).contentType( contentType );
        this.handlerValidate.handle( this.context, command );

        // test
        final DataValidationErrors result = command.getResult();
        assertFalse( result.hasErrors() );
        assertEquals( 0, result.size() );
    }

}
