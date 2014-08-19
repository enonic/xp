package com.enonic.wem.thymeleaf.internal;

import java.util.Map;
import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.Sets;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;

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
    public String process( final ResourceKey view, final Map<String, Object> params )
    {
        final Context context = new Context();
        context.setVariables( params );
        return this.engine.process( view.toString(), context );
    }
}
