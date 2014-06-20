package com.enonic.wem.portal.script.lib;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import com.google.common.base.Charsets;
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
        final URL path = service.resolveFile( name );

        return render( new InputStreamReader( path.openStream(), Charsets.UTF_8 ), map );
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
