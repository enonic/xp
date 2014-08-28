package com.enonic.wem.thymeleaf.internal;

import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.Sets;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.thymeleaf.ThymeleafRenderParams;

public final class ThymeleafProcessorImpl
    implements ThymeleafProcessor
{
    private final TemplateEngine engine;

    public ThymeleafProcessorImpl()
    {
        this.engine = new TemplateEngine();

        final Set<IDialect> dialects = Sets.newHashSet();
        dialects.add( new ThymeleafDialect() );
        dialects.add( new StandardDialect() );

        this.engine.setDialects( dialects );

        final TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setCacheable( false );
        templateResolver.setResourceResolver( new ThymeleafResourceResolver() );
        this.engine.setTemplateResolver( templateResolver );

        this.engine.initialize();
    }

    @Override
    public String render( final ThymeleafRenderParams params )
    {
        try
        {
            final Context context = new Context();
            context.setVariables( params.getParameters() );
            return this.engine.process( params.getView().toString(), context );
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
        return ResourceProblemException.newBuilder().
            lineNumber( e.getLineNumber() ).
            resource( ResourceKey.from( e.getTemplateName() ) ).
            cause( e ).
            message( e.getMessage() ).
            build();
    }
}
