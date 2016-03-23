package com.enonic.xp.site.mapping;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.Assert.*;

public class ControllerMappingDescriptorsTest
{

    @Test
    public void testEmpty()
        throws Exception
    {
        assertTrue( ControllerMappingDescriptors.empty().isEmpty() );
    }

    @Test
    public void testFrom()
        throws Exception
    {
        final ControllerMappingDescriptor descriptor1 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();

        final ControllerMappingDescriptor descriptor2 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/something.js" ) ).
            pattern( "/path/.*" ).
            contentConstraint( "type:'com.enonic.test.app:thing'" ).
            order( 15 ).
            build();

        final ControllerMappingDescriptor descriptor3 = ControllerMappingDescriptor.copyOf( descriptor1 ).build();

        assertEquals( 3, ControllerMappingDescriptors.from( descriptor1, descriptor2, descriptor3 ).getSize() );
        assertEquals( 3, ControllerMappingDescriptors.from( Arrays.asList( descriptor1, descriptor2, descriptor3 ) ).getSize() );
    }

}