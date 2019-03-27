package com.enonic.xp.site.mapping;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertNotNull( descriptor );
        Assert.assertEquals( ApplicationKey.from( "com.enonic.test.app" ), descriptor.getApplication() );
        Assert.assertEquals( ResourceKey.from( "com.enonic.test.app:/site/controllers/mycontroller.js" ), descriptor.getController() );
        Assert.assertNull( descriptor.getFilter() );
        Assert.assertTrue( descriptor.isController() );
        Assert.assertFalse( descriptor.isFilter() );
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
        Assert.assertNotNull( descriptor );
        Assert.assertTrue( descriptor.invertPattern() );
        Assert.assertEquals( ApplicationKey.from( "com.enonic.test.app" ), descriptor.getApplication() );
        Assert.assertEquals( ResourceKey.from( "com.enonic.test.app:/site/controllers/myfilter.js" ), descriptor.getFilter() );
        Assert.assertNull( descriptor.getController() );
        Assert.assertFalse( descriptor.isController() );
        Assert.assertTrue( descriptor.isFilter() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFilterOrController()
        throws Exception
    {
        final ControllerMappingDescriptor descriptor = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/mycontroller.js" ) ).
            filter( ResourceKey.from( ApplicationKey.from( "com.enonic.test.app" ), "/site/controllers/myfilter.js" ) ).
            pattern( "/people/.*" ).
            invertPattern( true ).
            contentConstraint( "type:'com.enonic.test.app:people'" ).
            order( 5 ).
            build();
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

        Assert.assertEquals(
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
        Assert.assertEquals( descriptor, copy );
        Assert.assertEquals( descriptor.hashCode(), copy.hashCode() );
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
        Assert.assertEquals( descriptor, copy );
        Assert.assertEquals( descriptor.hashCode(), copy.hashCode() );
        Assert.assertEquals( descriptor, descriptor );
        Assert.assertNotEquals( descriptor, descriptor.toString() );
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

        Assert.assertEquals( 1, descriptor.compareTo( descriptor2 ) );
    }
}