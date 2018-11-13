package com.enonic.xp.content;


import java.time.LocalDate;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;

import static org.junit.Assert.*;

public class ContentTest
{
    private static final ContentPath MY_CONTENT_PATH = ContentPath.from( "/mycontent" );

    @Test(expected = IllegalArgumentException.class)
    public void given_path_isRoot_then_IllegalArgumentException_is_thrown()
    {
        Content.create().path( ContentPath.ROOT ).build();
    }

    @Test
    public void isRoot_given_path_with_one_element_then_true_is_returned()
    {
        Content content = Content.create().path( "/myroot" ).build();
        assertEquals( true, content.isRoot() );
    }

    @Test
    public void isRoot_given_path_with_more_than_one_element_then_false_is_returned()
    {
        Content content = Content.create().path( "/myroot/mysub" ).build();
        assertEquals( false, content.isRoot() );
    }

    @Test
    public void given_array_when_setting_data_of_another_type_to_array_then_exception_is_thrown()
    {
        // setup
        Content content = Content.create().path( MY_CONTENT_PATH ).build();
        content.getData().setString( "myData", "Value 1" );

        // exercise
        try
        {
            content.getData().setLocalDate( "myData[1]", LocalDate.of( 2000, 1, 1 ) );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "This PropertyArray expects only properties with value of type 'String', got: LocalDate", e.getMessage() );
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_a_controller_and_a_pageTemplate_when_build_then_IllegalArgumentException_is_thrown()
    {
        Content.create().
            path( MY_CONTENT_PATH ).
            page( Page.create().
                descriptor( DescriptorKey.from( "abc:abc" ) ).
                template( PageTemplateKey.from( "123" ) ).
                config( new PropertyTree() ).
                regions( PageRegions.create().build() ).
                build() ).build();
    }
}
