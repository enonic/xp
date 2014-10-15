package com.enonic.wem.core.elasticsearch;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.index.UpdateScript;

public class UpdateScriptXContentBuilderFactoryTest
{


    @Test
    public void test_simple()
        throws Exception
    {
        final XContentBuilder xContentBuilder = UpdateScriptXContentBuilderFactory.create( UpdateScript.create().
            script( "this is my script" ).
            addParam( "param1", Value.newDouble( 3.0 ) ).
            addParam( "param2", Value.newString( "myValue" ) ).
            build() );

    }
}