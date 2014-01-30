package com.enonic.wem.portal.view.thymeleaf;

import javax.inject.Inject;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.view.RenderViewSpec;
import com.enonic.wem.portal.view.ViewProcessor;

public final class ThymeleafViewProcessor
    implements ViewProcessor
{
    private final TemplateEngine engine;

    @Inject
    public ThymeleafViewProcessor( final ModuleResourcePathResolver resolver )
    {
        this.engine = new TemplateEngine();
        this.engine.setDialect( new ThymeleafDialect() );

        final TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setCacheable( false );
        templateResolver.setResourceResolver( new ThymeleafResourceResolver( resolver ) );
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
