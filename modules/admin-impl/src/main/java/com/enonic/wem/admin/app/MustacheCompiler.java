package com.enonic.wem.admin.app;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public final class MustacheCompiler
{
    private final static MustacheCompiler INSTANCE = new MustacheCompiler();

    private final Mustache.Compiler compiler;

    private MustacheCompiler()
    {
        this.compiler = Mustache.compiler().escapeHTML( true );
    }

    public Template compile( final URL url )
    {
        final CharSource source = Resources.asCharSource( url, Charsets.UTF_8 );
        return compile( source );
    }

    public Template compile( final CharSource source )
    {
        try
        {
            return this.compiler.compile( source.read() );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    public static MustacheCompiler getInstance()
    {
        return INSTANCE;
    }
}
