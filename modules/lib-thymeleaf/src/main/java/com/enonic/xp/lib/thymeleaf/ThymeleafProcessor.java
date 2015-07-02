package com.enonic.xp.lib.thymeleaf;

import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;

import com.google.common.collect.Maps;

import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;

public final class ThymeleafProcessor
{
    private final TemplateEngine engine;

    private ResourceKey view;

    private final Map<String, Object> parameters;

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

    public String process()
    {
        try
        {
            final Context context = new Context();
            context.setVariables( this.parameters );
            return this.engine.process( this.view.toString(), context );
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
        final int lineNumber = e.getLineNumber() != null ? e.getLineNumber() : 0;
        final ResourceKey resource = e.getTemplateName() != null ? ResourceKey.from( e.getTemplateName() ) : null;
        return ResourceProblemException.newBuilder().
            lineNumber( lineNumber ).
            resource( resource ).
            cause( e ).
            message( e.getMessage() ).
            build();
    }
}
