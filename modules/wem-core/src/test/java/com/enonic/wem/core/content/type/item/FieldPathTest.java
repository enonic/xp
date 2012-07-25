package com.enonic.wem.core.content.type.item;


import org.junit.Test;

import static org.junit.Assert.*;

public class FieldPathTest
{
    @Test
    public void tostring()
    {
        assertEquals( "car", new FieldPath( "car" ).toString() );
        assertEquals( "car.model", new FieldPath( "car.model" ).toString() );
    }

    @Test
    public void new_given_existing_fieldPath_and_name()
    {
        assertEquals( "car.model", new FieldPath( new FieldPath( "car" ), "model" ).toString() );
    }
}
