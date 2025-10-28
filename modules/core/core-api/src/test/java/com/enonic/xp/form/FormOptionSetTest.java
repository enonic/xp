package com.enonic.xp.form;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormOptionSetTest
{
    private FormOptionSet set;

    @BeforeEach
    void before()
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
                defaultOption( true ).addFormItem(
                Input.create().name( "myTextLine2" ).helpText( "My text line 2 help text" ).label( "My text line 2" ).inputType(
                    InputTypeName.TEXT_LINE ).build() ).build() ).
            build();
    }

    @Test
    void testConstructor()
    {
        assertEquals( "myOptionSet", this.set.getName() );
        assertEquals( "My option set", this.set.getLabel() );
        assertEquals( "My option set help text", this.set.getHelpText() );

        List<FormOptionSetOption> options = StreamSupport.stream( this.set.spliterator(), false ).collect( Collectors.toList() );
        assertEquals( 2, options.size() );

        FormOptionSetOption option1 = options.get( 0 );
        assertFalse( option1.isDefaultOption() );

        assertThat( option1 ).size().isEqualTo( 1 );
        Input option1Input = (Input) option1.getInput( "myTextLine1" );
        assertEquals( "myTextLine1", option1Input.getName() );
        assertEquals( "My text line 1", option1Input.getLabel() );
        assertEquals( "My text line 1 help text", option1Input.getHelpText() );

        FormOptionSetOption option2 = options.get( 1 );
        assertTrue( option2.isDefaultOption() );

        assertThat( option2 ).size().isEqualTo( 1 );

        Input option2Input = option2.getInput( "myTextLine2" );
        assertEquals( "myTextLine2", option2Input.getName() );
        assertEquals( "My text line 2", option2Input.getLabel() );
        assertEquals( "My text line 2 help text", option2Input.getHelpText() );
    }

    @Test
    void testCopy()
    {
        final FormOptionSet copy = (FormOptionSet) this.set.copy();

        assertNotSame( this.set, copy );
        assertNull( copy.getParent() );
        assertEquals( this.set.getName(), copy.getName() );
        assertEquals( this.set.getLabel(), copy.getLabel() );
        assertEquals( this.set.getHelpText(), copy.getHelpText() );
        assertEquals( this.set.getOccurrences(), copy.getOccurrences() );
        assertEquals( this.set, copy );
    }

}
