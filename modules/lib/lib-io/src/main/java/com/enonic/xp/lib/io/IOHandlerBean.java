package com.enonic.xp.lib.io;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.LineProcessor;
import com.google.common.net.MediaType;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.util.MediaTypes;

public final class IOHandlerBean
    implements ScriptBean
{
    private BeanContext context;

    public String readText( final Object value )
        throws Exception
    {
        final CharSource source = toCharSource( value );
        return source.read();
    }

    public List<String> readLines( final Object value )
        throws Exception
    {
        final CharSource source = toCharSource( value );
        return source.readLines();
    }

    public void processLines( final Object value, Function<String, Object> callback )
        throws Exception
    {
        final CharSource source = toCharSource( value );
        source.readLines( new LineProcessor<Object>()
        {
            @Override
            public boolean processLine( final String line )
                throws IOException
            {
                callback.apply( line );
                return true;
            }

            @Override
            public Integer getResult()
            {
                return null;
            }
        } );
    }

    public long getSize( final Object value )
        throws Exception
    {
        return toByteSource( value ).size();
    }

    public ByteSource newStream( final String value )
    {
        return ByteSource.wrap( value.getBytes( Charsets.UTF_8 ) );
    }

    public String getMimeType( final Object key )
    {
        if ( key == null )
        {
            return MediaType.OCTET_STREAM.toString();
        }

        final MediaType type = MediaTypes.instance().fromFile( key.toString() );
        return type.toString();
    }

    public Resource getResource( final Object key )
    {
        final ResourceKey resourceKey = toResourceKey( key );
        final ResourceService service = this.context.getService( ResourceService.class ).get();
        return service.getResource( resourceKey );
    }

    private CharSource toCharSource( final Object value )
    {
        return toByteSource( value ).asCharSource( Charsets.UTF_8 );
    }

    private ByteSource toByteSource( final Object value )
    {
        if ( value instanceof ByteSource )
        {
            return (ByteSource) value;
        }

        return ByteSource.empty();
    }

    private ResourceKey toResourceKey( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof ResourceKey )
        {
            return (ResourceKey) value;
        }

        return this.context.getResourceKey().resolve( value.toString() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
    }
}
