package com.enonic.wem.core.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class FormItemsTest
{
    @Test
    public void getConfig()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).build();
        formItems.addFormItem( formItemSet );
        formItemSet.addField( Component.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addField( Component.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise & verify
        DirectAccessibleFormItem personaliaConfig =
            formItems.getDirectAccessibleFormItem( new FormItemPath( "personalia" ).getLastElement() );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        formItems.addFormItem( formItemSet );
        formItemSet.addField( Component.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addField( Component.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise & verify
        DirectAccessibleFormItem personaliaEyeColourConfig = formItemSet.getFormItems().getDirectAccessibleFormItem( "eyeColour" );
        assertEquals( "personalia.eyeColour", personaliaEyeColourConfig.getPath().toString() );
    }

    @Test
    public void toString_with_two_fields()
    {
        FormItems formItems = new FormItems();
        formItems.addFormItem( Component.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() );
        formItems.addFormItem( Component.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise & verify
        assertEquals( "eyeColour, hairColour", formItems.toString() );
    }

    @Test
    public void toString_with_visualFieldSet()
    {
        FormItems formItems = new FormItems();
        formItems.addFormItem( Component.newBuilder().name( "name" ).type( FieldTypes.TEXT_LINE ).build() );
        formItems.addFormItem( VisualFieldSet.newVisualFieldSet().label( "Visual" ).name( "visual" ).add(
            Component.newBuilder().name( "eyeColour" ).type( FieldTypes.TEXT_LINE ).build() ).add(
            Component.newBuilder().name( "hairColour" ).type( FieldTypes.TEXT_LINE ).build() ).build() );

        // exercise & verify
        assertEquals( "name, visual{eyeColour, hairColour}", formItems.toString() );
    }
}
