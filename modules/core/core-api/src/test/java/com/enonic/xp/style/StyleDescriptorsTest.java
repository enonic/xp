package com.enonic.xp.style;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class StyleDescriptorsTest
{

    @Test
    public void from()
    {
        StyleDescriptor styleDescriptor = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp" ) ).
            cssPath( "assets/styles.css" ).
            build();

        StyleDescriptor styleDescriptor2 = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp2" ) ).
            cssPath( "assets/styles.css" ).
            build();

        final StyleDescriptors styleDescriptors = StyleDescriptors.from( styleDescriptor, styleDescriptor2 );

        assertEquals( 2, styleDescriptors.getSize() );
    }

    @Test
    public void from1()
    {
        StyleDescriptor styleDescriptor = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp" ) ).
            cssPath( "assets/styles.css" ).
            build();

        StyleDescriptor styleDescriptor2 = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp2" ) ).
            cssPath( "assets/styles.css" ).
            build();

        final StyleDescriptors styleDescriptors = StyleDescriptors.from( Lists.newArrayList( styleDescriptor, styleDescriptor2 ) );

        assertEquals( 2, styleDescriptors.getSize() );
    }

    @Test
    public void empty()
    {
        final StyleDescriptors styleDescriptors = StyleDescriptors.empty();
        assertEquals( 0, styleDescriptors.getSize() );
    }
}