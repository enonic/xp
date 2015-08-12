package com.enonic.xp.page;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypeName;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.Assert.*;

public class PageDescriptorsTest
{
    @Test
    public void empty()
    {
        assertTrue( PageDescriptors.empty().isEmpty() );
    }

    @Test
    public void from()
    {
        final Form pageForm = Form.create().
            addFormItem( Input.create().name( "pause" ).label( "pause" ).inputType( InputTypeName.DOUBLE ).build() ).
            // add input of type region
                build();

        final PageDescriptor pageDescriptor1 = PageDescriptor.create().
            displayName( "Landing page" ).
            config( pageForm ).
            regions( RegionDescriptors.create().build() ).
            key( DescriptorKey.from( "module:landing-page" ) ).
            build();

        final PageDescriptor pageDescriptor2 = PageDescriptor.create().
            displayName( "Log out" ).
            config( pageForm ).
            regions( RegionDescriptors.create().build() ).
            key( DescriptorKey.from( "module:logout-page" ) ).
            build();

        final PageDescriptor pageDescriptor3 = PageDescriptor.copyOf( pageDescriptor1 ).build();

        assertEquals( 3, PageDescriptors.from( pageDescriptor1, pageDescriptor2, pageDescriptor3 ).getSize() );
        assertEquals( 3, PageDescriptors.from( Arrays.asList( pageDescriptor1, pageDescriptor2, pageDescriptor3 ) ).getSize() );
    }

}
