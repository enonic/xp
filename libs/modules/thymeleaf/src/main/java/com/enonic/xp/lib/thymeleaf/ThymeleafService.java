package com.enonic.xp.lib.thymeleaf;

import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.Sets;

import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.view.ViewFunctionService;

public final class ThymeleafService
{
    private final TemplateEngine engine;

    private ViewFunctionService viewFunctionService;

    public ThymeleafService()
    {
        this.engine = new TemplateEngine();

        final Set<IDialect> dialects = Sets.newHashSet();
        dialects.add( new ExtensionDialectImpl() );
        dialects.add( new StandardDialectImpl() );

        this.engine.setDialects( dialects );

        final TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setCacheable( false );
        templateResolver.setResourceResolver( new ThymeleafResourceResolver() );
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
        functions.viewFunctionService = this.viewFunctionService;
        functions.portalRequest = PortalRequestAccessor.get();
        return functions;
    }

    public void setViewFunctionService( final ViewFunctionService value )
    {
        this.viewFunctionService = value;
    }

}
