package com.enonic.xp.form;

import org.junit.jupiter.api.Test;

import static com.enonic.xp.form.FormItemPath.ELEMENT_DIVIDER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormItemPathTest
{
    @Test
    public void tostring()
    {
        assertEquals( "car", FormItemPath.from( "car" ).toString() );
        assertEquals( "car" + ELEMENT_DIVIDER + "model", FormItemPath.from( "car" + ELEMENT_DIVIDER + "model" ).toString() );
    }

    @Test
    public void new_given_existing_formItemPath_and_name()
    {
        assertEquals( "car" + ELEMENT_DIVIDER + "model", FormItemPath.from( FormItemPath.from( "car" ), "model" ).toString() );
    }

    @Test
    public void asNewWithoutFirstPathElement()
    {
        assertEquals( "", FormItemPath.from( "first" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second", FormItemPath.from( "first" + ELEMENT_DIVIDER + "second" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second" + ELEMENT_DIVIDER + "third", FormItemPath.from( "first" + ELEMENT_DIVIDER + "second" + ELEMENT_DIVIDER + "third" ).asNewWithoutFirstPathElement().toString() );
    }
}
