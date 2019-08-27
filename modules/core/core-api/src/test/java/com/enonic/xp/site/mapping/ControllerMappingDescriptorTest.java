package com.enonic.xp.site.mapping;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

public class ControllerMappingDescriptorTest
{

    @Test
    public void testCreateMappingController()
        throws Exception
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();
        assertNotNull( descriptor );
        assertEquals( ApplicationKey.from( "com.enonic.test.app" ), descriptor.getApplication() );
        assertEquals( ResourceKey.from( "com.enonic.test.app:/site/controllers/mycontroller.js" ), descriptor.getController() );
        assertNull( descriptor.getFilter() );
        assertTrue( descriptor.isController() );
        assertFalse( descriptor.isFilter() );
    }

    @Test
    public void testCreateMappingFilter()
        throws Exception
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            filter( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/myfilter.js" ) ).
            pattern( "/people/.*" ).
            invertPattern( true ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();
        assertNotNull( descriptor );
        assertTrue( descriptor.invertPattern() );
        assertEquals( ApplicationKey.from( "com.enonic.test.app" ), descriptor.getApplication() );
        assertEquals( ResourceKey.from( "com.enonic.test.app:/site/controllers/myfilter.js" ), descriptor.getFilter() );
        assertNull( descriptor.getController() );
        assertFalse( descriptor.isController() );
        assertTrue( descriptor.isFilter() );
    }

    @Test
    public void testCreateFilterOrController()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () ->  ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            filter( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/myfilter.js" ) ).
            pattern( "/people/.*" ).
            invertPattern( true ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build() );
    }

    @Test
    public void testToString()
        throws Exception
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();

        assertEquals(
            "ControllerMappingDescriptor{" + "controller=com.enonic.test.app:/site/controllers/mycontroller.js, " + "filter=null, " +
                "pattern=/people/.*, " + "invertPattern=false, " + "contentConstraint=type:'com.enonic.test.app:people', " + "order=5}",
            descriptor.toString() );
    }

    @Test
    public void testCopyOf()
        throws Exception
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();

        final ControllerMappingDescriptor copy = ControllerMappingDescriptor.copyOf( descriptor ).build();
        assertEquals( descriptor, copy );
        assertEquals( descriptor.hashCode(), copy.hashCode() );
    }

    @Test
    public void testEquals()
        throws Exception
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();

        final ControllerMappingDescriptor copy = ControllerMappingDescriptor.copyOf( descriptor ).
            pattern( Pattern.compile( "/people/.*" ) ).
            contentConstraint( ContentMappingConstraint.parse( "type:'com.enonic.test.app:people'" ) ).
            build();
        assertEquals( descriptor, copy );
        assertEquals( descriptor.hashCode(), copy.hashCode() );
        assertEquals( descriptor, descriptor );
        assertNotEquals( descriptor, descriptor.toString() );
    }

    @Test
    public void testCompare()
        throws Exception
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();
        final ControllerMappingDescriptor descriptor2 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 10 ).
            build();

        assertEquals( 1, descriptor.compareTo( descriptor2 ) );
    }
}
