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
}
