package com.enonic.xp.lib.thymeleaf;

import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.collect.Maps;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

public final class ThymeleafProcessor
{
    private final TemplateEngine engine;

    private ResourceKey view;

    private final Map<String, Object> parameters;

    private TemplateMode mode;

    public ThymeleafProcessor( final TemplateEngine engine, final ThymeleafViewFunctions viewFunctions )
    {
        this.engine = engine;
        this.parameters = Maps.newHashMap();

        this.parameters.put( "portal", viewFunctions );
    }

    public void setView( final ResourceKey view )
    {
        this.view = view;
    }

    public void setModel( final ScriptValue model )
    {
        if ( model != null )
        {
            this.parameters.putAll( model.getMap() );
        }
    }

    public void setMode( final String mode )
    {
        try
        {
            this.mode = TemplateMode.valueOf( mode.toUpperCase() );
        }
        catch ( final Exception e )
        {
            this.mode = TemplateMode.HTML;
        }
    }

    public String process()
    {
        final Trace trace = Tracer.newTrace( "thymeleaf.render" );
        if ( trace == null )
        {
            return doProcess();
        }

        return Tracer.trace( trace, () -> {
            trace.put( "path", this.view.getPath() );
            trace.put( "app", this.view.getApplicationKey().toString() );
            return doProcess();
        } );
    }

    private String doProcess()
    {
        try
        {
            final Context context = new Context();
            context.setVariables( this.parameters );

            final TemplateSpec spec = new TemplateSpec( this.view.toString(), this.mode );
            return this.engine.process( spec, context );
        }
        catch ( final RuntimeException e )
        {
            throw handleException( e );
        }
    }

    private RuntimeException handleException( final RuntimeException e )
    {
        if ( e instanceof TemplateProcessingException )
        {
            return handleException( (TemplateProcessingException) e );
        }

        return e;
    }

    private RuntimeException handleException( final TemplateProcessingException e )
    {
        final int lineNumber = e.getLine() != null ? e.getLine() : 0;
        final ResourceKey resource = e.getTemplateName() != null ? ResourceKey.from( e.getTemplateName() ) : null;
        return ResourceProblemException.create().
            lineNumber( lineNumber ).
            resource( resource ).
            cause( e ).
            message( e.getMessage() ).
            build();
    }
}
