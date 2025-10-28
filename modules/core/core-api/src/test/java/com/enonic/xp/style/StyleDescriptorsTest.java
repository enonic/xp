package com.enonic.xp.style;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyleDescriptorsTest
{

    @Test
    void from()
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
    void from1()
    {
        StyleDescriptor styleDescriptor = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp" ) ).
            cssPath( "assets/styles.css" ).
            build();

        StyleDescriptor styleDescriptor2 = StyleDescriptor.create().
            application( ApplicationKey.from( "myapp2" ) ).
            cssPath( "assets/styles.css" ).
            build();

        final StyleDescriptors styleDescriptors = StyleDescriptors.from( List.of( styleDescriptor, styleDescriptor2 ) );

        assertEquals( 2, styleDescriptors.getSize() );
    }

    @Test
    void empty()
    {
        final StyleDescriptors styleDescriptors = StyleDescriptors.empty();
        assertEquals( 0, styleDescriptors.getSize() );
    }
}
