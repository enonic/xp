package com.enonic.xp.portal.thymeleaf.impl;

import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;

final class ThymeleafProcessor
{
    private final TemplateEngine engine;

    private ResourceKey view;

    private final Map<String, Object> parameters;

    public ThymeleafProcessor( final TemplateEngine engine )
    {
        this.engine = engine;
        this.parameters = Maps.newHashMap();
    }

    public ThymeleafProcessor view( final ResourceKey view )
    {
        this.view = view;
        return this;
    }

    public ThymeleafProcessor parameters( final Map<String, Object> parameters )
    {
        if ( parameters != null )
        {
            this.parameters.putAll( parameters );
        }

        return this;
    }

    public ThymeleafProcessor parameter( final String key, final Object value )
    {
        this.parameters.put( key, value );
        return this;
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
