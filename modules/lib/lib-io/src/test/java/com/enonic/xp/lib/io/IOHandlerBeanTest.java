package com.enonic.xp.lib.io;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IOHandlerBeanTest
    extends ScriptTestSupport
{
    private IOHandlerBean bean;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.bean = new IOHandlerBean();
        this.bean.initialize( newBeanContext( ResourceKey.from( "myapp:/test" ) ) );
    }

    @Test
    void readText()
        throws Exception
    {
        final String text1 = this.bean.readText( null );
        assertEquals( "", text1 );

        final String text2 = this.bean.readText( "value" );
        assertEquals( "", text2 );

        final String text3 = this.bean.readText( bean.newStream( "value" ) );
        assertEquals( "value", text3 );
    }

    @Test
    void readLines()
        throws Exception
    {
        final List<String> lines = this.bean.readLines( bean.newStream( "value1\nvalue2\n" ) );
        assertEquals( 2, lines.size() );
    }

    @Test
    void processLines()
        throws Exception
    {
        final StringBuilder result = new StringBuilder();
        this.bean.processLines( bean.newStream( "value1\nvalue2\n" ), line -> {
            result.append( "-" ).append( line );
            return null;
        } );

        assertEquals( "-value1-value2", result.toString() );
    }

    @Test
    void getSize()
        throws Exception
    {
        final long size = this.bean.getSize( bean.newStream( "value" ) );
        assertEquals( 5, size );
    }

    @Test
    void getMimeType()
    {
        final String type1 = this.bean.getMimeType( null );
        assertEquals( "application/octet-stream", type1 );

        final String type2 = this.bean.getMimeType( "test.txt" );
        assertEquals( "text/plain", type2 );
    }

    @Test
    void getResource()
    {
        final Resource res1 = this.bean.getResource( "/unknown.txt" );
        assertEquals( false, res1.exists() );

        final Resource res2 = this.bean.getResource( "./sample.txt" );
        assertEquals( true, res2.exists() );

        final Resource res3 = this.bean.getResource( ResourceKey.from( "myapp:/test/sample.txt" ) );
        assertEquals( true, res3.exists() );
    }
}
