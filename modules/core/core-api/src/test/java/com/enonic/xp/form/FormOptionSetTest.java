package com.enonic.xp.form;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.Assert.*;

public class FormOptionSetTest
{
    private FormOptionSet set;

    @Before
    public void before()
    {
        this.set = FormOptionSet.create().
            name( "myOptionSet" ).
            label( "My option set" ).
            helpText( "My option set help text" ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption1" ).label( "option label1" ).
                addFormItem(
                    Input.create().name( "myTextLine1" ).helpText( "My text line 1 help text" ).label( "My text line 1" ).inputType(
                        InputTypeName.TEXT_LINE ).build() ).build() ).
            addOptionSetOption( FormOptionSetOption.create().name( "myOptionSetOption2" ).label( "option label2" ).
                isDefaultOption( true ).addFormItem(
                Input.create().name( "myTextLine2" ).helpText( "My text line 2 help text" ).label( "My text line 2" ).inputType(
                    InputTypeName.TEXT_LINE ).build() ).build() ).
            build();
    }

    @Test
    public void testConstructor()
    {
        assertEquals( "myOptionSet", this.set.getName() );
        assertEquals( "My option set", this.set.getLabel() );
        assertEquals( "My option set help text", this.set.getHelpText() );

        List<FormOptionSetOption> options = this.set.getOptions();
        assertEquals( 2, options.size() );

        FormOptionSetOption option1 = options.get( 0 );
        assertFalse( option1.isDefaultOption() );

        List<FormItem> option1Items = option1.getFormItems();
        assertEquals( 1, option1Items.size() );
        Input option1Input = (Input) option1Items.get( 0 );
        assertEquals( "myTextLine1", option1Input.getName() );
        assertEquals( "My text line 1", option1Input.getLabel() );
        assertEquals( "My text line 1 help text", option1Input.getHelpText() );

        FormOptionSetOption option2 = options.get( 1 );
        assertTrue( option2.isDefaultOption() );

        List<FormItem> option2Items = option2.getFormItems();
        assertEquals( 1, option2Items.size() );
        Input option2Input = (Input) option2Items.get( 0 );
        assertEquals( "myTextLine2", option2Input.getName() );
        assertEquals( "My text line 2", option2Input.getLabel() );
        assertEquals( "My text line 2 help text", option2Input.getHelpText() );
    }

    @Test
    public void testCopy()
    {
        final FormOptionSet copy = (FormOptionSet) this.set.copy();

        assertEquals( this.set.getName(), copy.getName() );
        assertEquals( this.set.getLabel(), copy.getLabel() );
        assertEquals( this.set.getHelpText(), copy.getHelpText() );
        assertEquals( this.set.getOccurrences(), copy.getOccurrences() );
        assertEquals( this.set.getOptions(), copy.getOptions() );
        assertTrue( this.set.equals( copy ) );
    }

}
