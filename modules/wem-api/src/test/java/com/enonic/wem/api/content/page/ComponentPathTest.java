package com.enonic.wem.api.content.page;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ComponentPathTest
{
    @Test(expected = IllegalArgumentException.class)
    public void from_throws_IllegalArgumentException_when_odd_number_of_path_elements()
    {
        ComponentPath.from( "my-region" );
    }

    @Test
    public void tostring()
    {
        assertEquals( "my-region/my-component", ComponentPath.from( "my-region/my-component" ).toString() );
        assertEquals( "my-region/my-layout/left/my-component", ComponentPath.from( "my-region/my-layout/left/my-component" ).toString() );
    }

    @Test
    public void removeFirstLevel()
    {
        assertEquals( "left/my-component", ComponentPath.from( "my-region/my-layout/left/my-component" ).removeFirstLevel().toString() );
        assertNull( ComponentPath.from( "my-region/my-layout" ).removeFirstLevel() );
    }
}
