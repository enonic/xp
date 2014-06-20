package com.enonic.wem.portal.view.thymeleaf;

import java.util.Set;

import javax.inject.Inject;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.Sets;

import com.enonic.wem.portal.view.RenderViewSpec;
import com.enonic.wem.portal.view.ViewProcessor;

public final class ThymeleafViewProcessor
    implements ViewProcessor
{
    private final TemplateEngine engine;

    @Inject
    public ThymeleafViewProcessor()
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
    public String getName()
    {
        return "thymeleaf";
    }

    @Override
    public String process( final RenderViewSpec spec )
    {
        final Context context = new Context();
        context.setVariables( spec.getParams() );
        return this.engine.process( spec.getView().toString(), context );
    }
}
