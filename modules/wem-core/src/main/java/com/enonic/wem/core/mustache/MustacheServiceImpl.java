package com.enonic.wem.core.mustache;

import java.io.Reader;
import java.util.Map;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public final class MustacheServiceImpl
    implements MustacheService
{
    private final Mustache.Compiler compiler;

    public MustacheServiceImpl()
    {
        this.compiler = Mustache.compiler();
    }

    @Override
    public String render( final Reader reader, final Map<String, Object> model )
    {
        final Template template = this.compiler.compile( reader );
        return template.execute( model );
    }
}
