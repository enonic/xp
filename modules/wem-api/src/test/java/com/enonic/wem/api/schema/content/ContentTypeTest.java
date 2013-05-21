package com.enonic.wem.api.schema.content;


import org.junit.Test;

import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.form.FieldSet;
import com.enonic.wem.api.schema.content.form.FormItemSet;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public class ContentTypeTest
{
    private static final ModuleName MY_MODULE_NAME = ModuleName.from( "mymodule" );

    @Test
    public void layout()
    {
        ContentType contentType = newContentType().name( "test" ).module( MY_MODULE_NAME ).build();
        FieldSet layout = FieldSet.newFieldSet().
            label( "Personalia" ).
            name( "personalia" ).
            add( newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        contentType.form().addFormItem( layout );

        assertEquals( "eyeColour", contentType.form().getInput( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout_inside_formItemSet()
    {
        ContentType contentType = newContentType().name( "test" ).module( MY_MODULE_NAME ).build();

        FieldSet layout = FieldSet.newFieldSet().
            label( "Personalia" ).
            name( "personalia" ).
            add( newInput().name( "eyeColour" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        FormItemSet myFormItemSet = newFormItemSet().name( "mySet" ).addFormItem( layout ).build();
        contentType.form().addFormItem( myFormItemSet );

        assertEquals( "mySet.eyeColour", contentType.form().getInput( "mySet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        FormItemSet formItemSet = newFormItemSet().name( "address" ).build();
        formItemSet.add( newInput().name( "label" ).label( "Label" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "street" ).label( "Street" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalNo" ).label( "Postal No" ).inputType( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "country" ).label( "Country" ).inputType( InputTypes.TEXT_LINE ).build() );

        ContentType contentType = newContentType().
            name( "test" ).
            module( MY_MODULE_NAME ).
            addFormItem( newInput().name( "title" ).inputType( InputTypes.TEXT_LINE ).build() ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "title", contentType.form().getInput( "title" ).getPath().toString() );
        assertEquals( "address.label", contentType.form().getInput( "address.label" ).getPath().toString() );
        assertEquals( "address.street", contentType.form().getInput( "address.street" ).getPath().toString() );
        assertEquals( "address.postalNo", contentType.form().getInput( "address.postalNo" ).getPath().toString() );
        assertEquals( "address.country", contentType.form().getInput( "address.country" ).getPath().toString() );
    }

    @Test
    public void formItemSet_in_formItemSet()
    {
        FormItemSet formItemSet = newFormItemSet().name( "top-set" ).addFormItem(
            newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build() ).addFormItem(
            newFormItemSet().name( "inner-set" ).addFormItem(
                newInput().name( "myInnerInput" ).inputType( InputTypes.TEXT_LINE ).build() ).build() ).build();
        ContentType contentType = newContentType().
            name( "test" ).
            module( MY_MODULE_NAME ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "top-set", contentType.form().getFormItemSet( "top-set" ).getPath().toString() );
        assertEquals( "top-set.myInput", contentType.form().getInput( "top-set.myInput" ).getPath().toString() );
        assertEquals( "top-set.inner-set", contentType.form().getFormItemSet( "top-set.inner-set" ).getPath().toString() );
        assertEquals( "top-set.inner-set.myInnerInput",
                      contentType.form().getInput( "top-set.inner-set.myInnerInput" ).getPath().toString() );
    }
}
