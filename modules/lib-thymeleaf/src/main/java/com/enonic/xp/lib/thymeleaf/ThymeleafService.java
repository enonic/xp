package com.enonic.xp.lib.thymeleaf;

import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.Sets;

import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.bean.ScriptBean;
import com.enonic.xp.portal.view.ViewFunctionService;

public final class ThymeleafService
    implements ScriptBean
{
    private final TemplateEngine engine;

    private ThymeleafResourceResolver thymeleafResourceResolver;

    private BeanContext context;

    public ThymeleafService()
    {
        this.engine = new TemplateEngine();

        final Set<IDialect> dialects = Sets.newHashSet();
        dialects.add( new ExtensionDialectImpl() );
        dialects.add( new StandardDialectImpl() );

        this.engine.setDialects( dialects );

        final TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setCacheable( false );
        this.thymeleafResourceResolver = new ThymeleafResourceResolver();
        templateResolver.setResourceResolver( this.thymeleafResourceResolver );
        this.engine.setTemplateResolver( templateResolver );

        this.engine.initialize();
    }

    public ThymeleafProcessor newProcessor()
    {
        return new ThymeleafProcessor( this.engine, createViewFunctions() );
    }

    private ThymeleafViewFunctions createViewFunctions()
    {
        final ThymeleafViewFunctions functions = new ThymeleafViewFunctions();
        functions.viewFunctionService = this.context.getService( ViewFunctionService.class ).get();
        functions.portalRequest = this.context.getRequest().get();
        return functions;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
        this.thymeleafResourceResolver.initialize( context );
    }
}
