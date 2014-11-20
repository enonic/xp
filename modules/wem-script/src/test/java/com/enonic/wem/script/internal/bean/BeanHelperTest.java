package com.enonic.wem.script.internal.bean;

import java.util.Map;

import org.junit.Test;

import com.google.common.base.Joiner;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;

public class BeanHelperTest
{
    @Test
    public void testSomething()
    {
        final Content content = Content.newContent().
            parentPath( ContentPath.from( "/test" ) ).
            name( "test" ).
            build();

        final Map<String, Object> map = BeanHelper.getAsMap( content );
        System.out.println( Joiner.on( ", " ).withKeyValueSeparator( " = " ).join( map ) );

    }
}
