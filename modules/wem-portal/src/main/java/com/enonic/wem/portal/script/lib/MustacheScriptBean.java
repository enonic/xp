package com.enonic.wem.portal.script.lib;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Map;

import com.samskivert.mustache.Mustache;

public final class MustacheScriptBean
{
    private final Mustache.Compiler compiler;

    public MustacheScriptBean()
    {
        this.compiler = Mustache.compiler();
    }

    public String render( final String name, final Map<String, Object> map )
        throws Exception
    {
        final ContextScriptBean service = ContextScriptBean.get();
        final Path path = service.resolveFile( name );
        return render( new FileReader( path.toFile() ), map );
    }

    public String renderText( final String text, final Map<String, Object> map )
    {
        return render( new StringReader( text ), map );
    }

    private String render( final Reader reader, final Map<String, Object> map )
    {
        return this.compiler.compile( reader ).execute( map );
    }
}
