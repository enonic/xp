package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.api.content.ModuleBasedQualifiedName;

import static org.junit.Assert.*;

public class ModuleBasedQualifiedNameTest
{
    @Test
    public void contructor()
    {
        ModuleBasedQualifiedName tqn = new ModuleBasedQualifiedName( "myModule:myLocalName" )
        {

        };
        assertEquals( "myModule", tqn.getModuleName() );
        assertEquals( "myLocalName", tqn.getLocalName() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void contructor_throws_exception_when_missing_colon()
    {
        new ModuleBasedQualifiedName( "myModule" )
        {

        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void contructor_throws_exception_when_missing_subType_name()
    {
        new ModuleBasedQualifiedName( "myModule:" )
        {

        };
    }

    @Test(expected = IllegalArgumentException.class)
    public void contructor_throws_exception_when_missing_module_name()
    {
        new ModuleBasedQualifiedName( ":myLocalName" )
        {

        };
    }
}
