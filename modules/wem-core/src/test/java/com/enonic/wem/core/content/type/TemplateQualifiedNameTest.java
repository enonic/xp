package com.enonic.wem.core.content.type;


import org.junit.Test;

import static org.junit.Assert.*;

public class TemplateQualifiedNameTest
{
    @Test
    public void contructor()
    {
        TemplateQualifiedName tqn = new TemplateQualifiedName( "myModule:myTemplate" );
        assertEquals( "myModule", tqn.getModuleName() );
        assertEquals( "myTemplate", tqn.getTemplateName() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void contructor_throws_exception_when_missing_colon()
    {
        new TemplateQualifiedName( "myModule" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void contructor_throws_exception_when_missing_template_name()
    {
        new TemplateQualifiedName( "myModule:" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void contructor_throws_exception_when_missing_module_name()
    {
        new TemplateQualifiedName( ":myTemplate" );
    }
}
