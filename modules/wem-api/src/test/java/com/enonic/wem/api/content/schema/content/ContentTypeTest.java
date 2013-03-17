package com.enonic.wem.api.content.schema.content;


import org.junit.Test;

import com.enonic.wem.api.content.schema.content.form.FieldSet;
import com.enonic.wem.api.content.schema.content.form.FormItemSet;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;
import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public class ContentTypeTest
{
    @Test
    public void layout()
    {
        ContentType contentType = newContentType().name( "test" ).module( ModuleName.from( "myModule" ) ).build();
        FieldSet layout = FieldSet.newFieldSet().
            label( "Personalia" ).
            name( "personalia" ).
            add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).
            build();

        contentType.form().addFormItem( layout );

        assertEquals( "eyeColour", contentType.form().getInput( "eyeColour" ).getPath().toString() );
    }

    @Test
    public void layout_inside_formItemSet()
    {
        ContentType contentType = newContentType().name( "test" ).module( ModuleName.from( "myModule" ) ).build();

        FieldSet layout = FieldSet.newFieldSet().
            label( "Personalia" ).
            name( "personalia" ).
            add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).
            build();

        FormItemSet myFormItemSet = newFormItemSet().name( "mySet" ).addFormItem( layout ).build();
        contentType.form().addFormItem( myFormItemSet );

        assertEquals( "mySet.eyeColour", contentType.form().getInput( "mySet.eyeColour" ).getPath().toString() );
    }

    @Test
    public void address()
    {
        FormItemSet formItemSet = newFormItemSet().name( "address" ).build();
        formItemSet.add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() );

        ContentType contentType = newContentType().
            name( "test" ).
            module( ModuleName.from( "myModule" ) ).
            addFormItem( newInput().name( "title" ).type( InputTypes.TEXT_LINE ).build() ).
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
        FormItemSet formItemSet =
            newFormItemSet().name( "top-set" ).addFormItem( newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build() ).addFormItem(
                newFormItemSet().name( "inner-set" ).addFormItem(
                    newInput().name( "myInnerInput" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();
        ContentType contentType = newContentType().
            name( "test" ).
            module( ModuleName.from( "myModule" ) ).
            addFormItem( formItemSet ).
            build();

        assertEquals( "top-set", contentType.form().getFormItemSet( "top-set" ).getPath().toString() );
        assertEquals( "top-set.myInput", contentType.form().getInput( "top-set.myInput" ).getPath().toString() );
        assertEquals( "top-set.inner-set", contentType.form().getFormItemSet( "top-set.inner-set" ).getPath().toString() );
        assertEquals( "top-set.inner-set.myInnerInput",
                      contentType.form().getInput( "top-set.inner-set.myInnerInput" ).getPath().toString() );
    }
}
