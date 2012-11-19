package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import static org.junit.Assert.*;

public class FormItemPathTest
{
    @Test
    public void tostring()
    {
        assertEquals( "car", new FormItemPath( "car" ).toString() );
        assertEquals( "car.model", new FormItemPath( "car.model" ).toString() );
    }

    @Test
    public void new_given_existing_componentPath_and_name()
    {
        assertEquals( "car.model", new FormItemPath( new FormItemPath( "car" ), "model" ).toString() );
    }

    @Test
    public void asNewWithoutFirstPathElement()
    {
        assertEquals( "", new FormItemPath( "first" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second", new FormItemPath( "first.second" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second.third", new FormItemPath( "first.second.third" ).asNewWithoutFirstPathElement().toString() );
    }
}
