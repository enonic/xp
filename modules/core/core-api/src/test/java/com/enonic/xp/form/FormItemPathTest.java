package com.enonic.xp.form;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormItemPathTest
{
    @Test
    public void tostring()
    {
        assertEquals( "car", FormItemPath.from( "car" ).toString() );
        assertEquals( "car.model", FormItemPath.from( "car.model" ).toString() );
    }

    @Test
    public void new_given_existing_formItemPath_and_name()
    {
        assertEquals( "car.model", FormItemPath.from( FormItemPath.from( "car" ), "model" ).toString() );
    }

    @Test
    public void asNewWithoutFirstPathElement()
    {
        assertEquals( "", FormItemPath.from( "first" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second", FormItemPath.from( "first.second" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second.third", FormItemPath.from( "first.second.third" ).asNewWithoutFirstPathElement().toString() );
    }
}
