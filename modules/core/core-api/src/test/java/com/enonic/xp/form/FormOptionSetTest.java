package com.enonic.xp.form;

import org.junit.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.Assert.*;

public class FormOptionSetTest
{

    @Test
    public void testCopy()
    {
        final FormOptionSet formOptionSet = FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).
                addFormItem(
                    Input.create().name( "myTextLine1" ).label( "myTextLine1" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).
                addFormItem(
                    Input.create().name( "myTextLine2" ).label( "myTextLine2" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        final FormOptionSet copy = (FormOptionSet) formOptionSet.copy();

        assertEquals( formOptionSet.getName(), copy.getName() );
        assertEquals( formOptionSet.getLabel(), copy.getLabel() );
        assertEquals( formOptionSet.getOccurrences(), copy.getOccurrences() );
        assertEquals( formOptionSet.getOptions(), copy.getOptions() );
        assertTrue( formOptionSet.equals( copy ) );
    }

}
