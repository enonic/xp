package com.enonic.xp.lib.io;

import java.util.List;

import org.junit.Test;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptBeanTestSupport;

import static org.junit.Assert.*;

public class IOHandlerBeanTest
    extends ScriptBeanTestSupport
{
    private IOHandlerBean bean;

    @Override
    protected void initialize()
    {
        super.initialize();

        this.bean = new IOHandlerBean();
        this.bean.initialize( newBeanContext( ResourceKey.from( "myapp:/site/test" ) ) );
    }

    @Test
    public void readText()
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
    public void readLines()
        throws Exception
    {
        final List<String> lines = this.bean.readLines( bean.newStream( "value1\nvalue2\n" ) );
        assertEquals( 2, lines.size() );
    }

    @Test
    public void processLines()
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
    public void getSize()
        throws Exception
    {
        final long size = this.bean.getSize( bean.newStream( "value" ) );
        assertEquals( 5, size );
    }

    @Test
    public void getMimeType()
        throws Exception
    {
        final String type1 = this.bean.getMimeType( null );
        assertEquals( "application/octet-stream", type1 );

        final String type2 = this.bean.getMimeType( "test.txt" );
        assertEquals( "text/plain", type2 );
    }

    @Test
    public void getResource()
        throws Exception
    {
        final Resource res1 = this.bean.getResource( "/unknown.txt" );
        assertEquals( false, res1.exists() );

        final Resource res2 = this.bean.getResource( "./sample.txt" );
        assertEquals( true, res2.exists() );

        final Resource res3 = this.bean.getResource( ResourceKey.from( "myapp:/site/test/sample.txt" ) );
        assertEquals( true, res3.exists() );
    }
}
