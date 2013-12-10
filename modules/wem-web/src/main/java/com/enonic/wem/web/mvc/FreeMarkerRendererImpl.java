package com.enonic.wem.web.mvc;

import java.io.IOException;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public final class FreeMarkerRendererImpl
    implements FreeMarkerRenderer
{
    private final Configuration config;

    @Inject
    public FreeMarkerRendererImpl( final ServletContext context )
    {
        this.config = new Configuration();
        this.config.setServletContextForTemplateLoading( context, "/WEB-INF/views" );
    }

    public String render( final FreeMarkerView view )
        throws IOException
    {
        final StringWriter out = new StringWriter();
        final Template template = this.config.getTemplate( view.getTemplate() );

        try
        {
            template.process( view.getModel(), out );
        }
        catch ( final TemplateException e )
        {
            // Do nothing
        }

        return out.toString();
    }
}
