package com.enonic.wem.portal.script.lib;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public final class MustacheScriptBean
{
    private final MustacheFactory factory;

    public MustacheScriptBean()
    {
        this.factory = new DefaultMustacheFactory();
    }

    public String render( final String name, final Map<String, Object> map )
        throws Exception
    {
        final ContextScriptBean service = ContextScriptBean.get();
        final Path path = service.resolveFile( name );
        return render( new FileReader( path.toFile() ), name, map );
    }

    public String renderText( final String text, final Map<String, Object> map )
    {
        return render( new StringReader( text ), "unknown", map );
    }

    private String render( final Reader reader, final String name, final Map<String, Object> map )
    {
        final Mustache mustache = this.factory.compile( reader, name );
        final StringWriter out = new StringWriter();

        mustache.execute( out, map );
        return out.toString();
    }
}
