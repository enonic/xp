package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigItemPathTest
{
    @Test
    public void tostring()
    {
        assertEquals( "car", new ConfigItemPath( "car" ).toString() );
        assertEquals( "car.model", new ConfigItemPath( "car.model" ).toString() );
    }

    @Test
    public void new_given_existing_configItemPath_and_name()
    {
        assertEquals( "car.model", new ConfigItemPath( new ConfigItemPath( "car" ), "model" ).toString() );
    }

    @Test
    public void asNewWithoutFirstPathElement()
    {
        assertEquals( "", new ConfigItemPath( "first" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second", new ConfigItemPath( "first.second" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second.third", new ConfigItemPath( "first.second.third" ).asNewWithoutFirstPathElement().toString() );
    }
}
