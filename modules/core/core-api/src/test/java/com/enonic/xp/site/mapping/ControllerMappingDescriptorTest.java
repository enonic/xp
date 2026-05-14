package com.enonic.xp.site.mapping;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControllerMappingDescriptorTest
{

    @Test
    void testCreateMappingController()
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
    void testCreateMappingFilter()
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
    void testCreateFilterOrController()
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
    void testToString()
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();

        assertEquals(
            "ControllerMappingDescriptor{service=null, " + "controller=com.enonic.test.app:/site/controllers/mycontroller.js, " +
                "filter=null, " + "pattern=/people/.*, " + "invertPattern=false, " +
                "contentConstraint=type:'com.enonic.test.app:people', " + "order=5}",
            descriptor.toString() );
    }

    @Test
    void testCopyOf()
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
    void testEquals()
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
    void testAppTokenSubstitutedInPattern()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "com.foo.bar" );

        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( applicationKey, "/site/controllers/mycontroller.js" ) ).
            pattern( "/_/static/${app}/.+" ).
            build();

        final String expected = "/_/static/" + Pattern.quote( "com.foo.bar" ) + "/.+";
        assertEquals( expected, descriptor.getPattern().pattern() );
        assertTrue( descriptor.getPattern().matcher( "/_/static/com.foo.bar/main.js" ).matches() );
        assertFalse( descriptor.getPattern().matcher( "/_/static/com.other.app/main.js" ).matches() );
    }

    @Test
    void testAppTokenAbsentIsNoOp()
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.foo.bar" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/people/.*" ).
            build();

        assertEquals( "/people/.*", descriptor.getPattern().pattern() );
    }

    @Test
    void testAppTokenExpansionIsRegexQuoted()
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.foo.bar" ), "/site/controllers/mycontroller.js" ) ).
            pattern( "/_/static/${app}/.+" ).
            build();

        assertFalse( descriptor.getPattern().matcher( "/_/static/comXfooYbar/main.js" ).matches() );
    }

    @Test
    void testAppTokenSubstitutedForFilter()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "com.foo.bar" );

        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            filter( ResourceKey.from( applicationKey, "/site/filters/myfilter.js" ) ).
            pattern( "/_/static/${app}/.+" ).
            build();

        assertTrue( descriptor.getPattern().matcher( "/_/static/com.foo.bar/main.js" ).matches() );
    }

    @Test
    void testExplicitApplicationKeyTakesPrecedence()
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.foo.bar" ), "/site/controllers/mycontroller.js" ) ).
            applicationKey( ApplicationKey.from( "com.override.app" ) ).
            pattern( "/_/static/${app}/.+" ).
            build();

        assertTrue( descriptor.getPattern().matcher( "/_/static/com.override.app/main.js" ).matches() );
        assertFalse( descriptor.getPattern().matcher( "/_/static/com.foo.bar/main.js" ).matches() );
    }

    @Test
    void testCompare()
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
