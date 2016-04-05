package com.enonic.xp.site.mapping;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

public class ControllerMappingDescriptorTest
{

    @Test
    public void testCreate()
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

        Assert.assertEquals( "ControllerMappingDescriptor{" +
                                 "controller=com.enonic.test.app:/site/controllers/mycontroller.js, " +
                                 "pattern=/people/.*, " +
                                 "invertPattern=false, " +
                                 "contentConstraint=type:'com.enonic.test.app:people', " +
                                 "order=5}", descriptor.toString() );
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