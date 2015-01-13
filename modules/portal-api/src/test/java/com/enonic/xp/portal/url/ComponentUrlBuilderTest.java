package com.enonic.xp.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class ComponentUrlBuilderTest
    extends AbstractUrlBuilderTest
{
    @Test
    public void createUrl()
    {
        final ComponentUrlBuilder builder = this.builders.componentUrl().
            component( "mycomp" ).
            param( "a", 3 );

        assertEquals( "/portal/stage/some/path/_/component/mycomp?a=3", builder.toString() );
    }
}

