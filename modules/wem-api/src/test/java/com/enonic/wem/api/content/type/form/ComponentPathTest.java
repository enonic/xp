package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import static org.junit.Assert.*;

public class ComponentPathTest
{
    @Test
    public void tostring()
    {
        assertEquals( "car", new ComponentPath( "car" ).toString() );
        assertEquals( "car.model", new ComponentPath( "car.model" ).toString() );
    }

    @Test
    public void new_given_existing_componentPath_and_name()
    {
        assertEquals( "car.model", new ComponentPath( new ComponentPath( "car" ), "model" ).toString() );
    }

    @Test
    public void asNewWithoutFirstPathElement()
    {
        assertEquals( "", new ComponentPath( "first" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second", new ComponentPath( "first.second" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second.third", new ComponentPath( "first.second.third" ).asNewWithoutFirstPathElement().toString() );
    }
}
