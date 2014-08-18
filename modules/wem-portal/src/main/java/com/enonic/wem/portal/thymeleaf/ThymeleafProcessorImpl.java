package com.enonic.wem.portal.thymeleaf;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.Sets;

import com.enonic.wem.api.resource.ResourceKey;

public final class ThymeleafProcessorImpl
    implements ThymeleafProcessor
{
    private final TemplateEngine engine;

    @Inject
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
