package com.enonic.wem.api.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;

import static com.enonic.wem.api.content.type.formitem.Input.newInput;
import static org.junit.Assert.*;

public class FormItemsTest
{
    @Test
    public void getConfig()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).build();
        formItems.addFormItem( formItemSet );
        formItemSet.addItem( newInput().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( newInput().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalFormItem personaliaConfig = formItems.getHierarchicalFormItem( new FormItemPath( "personalia" ).getLastElement() );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        formItems.addFormItem( formItemSet );
        formItemSet.addItem( newInput().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( newInput().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalFormItem personaliaEyeColourConfig = formItemSet.getFormItems().getHierarchicalFormItem( "eyeColour" );
        assertEquals( "personalia.eyeColour", personaliaEyeColourConfig.getPath().toString() );
    }

    @Test
    public void toString_with_two_fields()
    {
        FormItems formItems = new FormItems();
        formItems.addFormItem( newInput().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItems.addFormItem( newInput().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() );

        // exercise & verify
        assertEquals( "eyeColour, hairColour", formItems.toString() );
    }

    @Test
    public void toString_with_layout()
    {
        FormItems formItems = new FormItems();
        formItems.addFormItem( newInput().name( "name" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItems.addFormItem( FieldSet.newFieldSet().label( "Layout" ).name( "layout" ).add(
            newInput().name( "eyeColour" ).type( ComponentTypes.TEXT_LINE ).build() ).add(
            newInput().name( "hairColour" ).type( ComponentTypes.TEXT_LINE ).build() ).build() );

        // exercise & verify
        assertEquals( "name, layout{eyeColour, hairColour}", formItems.toString() );
    }
}
