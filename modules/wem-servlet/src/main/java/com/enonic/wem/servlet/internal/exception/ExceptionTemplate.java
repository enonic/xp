package com.enonic.wem.servlet.internal.exception;

import java.net.URL;

import com.samskivert.mustache.Template;

import com.enonic.wem.core.mustache.MustacheCompiler;

final class ExceptionTemplate
{
    private final Template template;

    public ExceptionTemplate()
    {
        final URL url = getClass().getResource( "error.html" );
        this.template = MustacheCompiler.getInstance().compile( url );
    }

    public String render( final StatusErrorInfo info )
    {
        return this.template.execute( info );
    }
}
