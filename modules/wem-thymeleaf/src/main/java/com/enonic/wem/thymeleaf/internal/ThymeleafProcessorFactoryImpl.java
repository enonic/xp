package com.enonic.wem.thymeleaf.internal;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.Sets;

import com.enonic.wem.portal.view.ViewFunctions;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

@Component
public final class ThymeleafProcessorFactoryImpl
    implements ThymeleafProcessorFactory
{
    private final TemplateEngine engine;

    private ViewFunctions viewFunctions;

    public ThymeleafProcessorFactoryImpl()
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

    @Override
    public ThymeleafProcessor newProcessor()
    {
        return new ThymeleafProcessorImpl( this.engine, this.viewFunctions );
    }

    @Reference
    public void setViewFunctions( final ViewFunctions viewFunctions )
    {
        this.viewFunctions = viewFunctions;
    }
}
