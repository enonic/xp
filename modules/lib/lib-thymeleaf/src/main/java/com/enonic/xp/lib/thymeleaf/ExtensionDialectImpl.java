package com.enonic.xp.lib.thymeleaf;

import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

final class ExtensionDialectImpl
    extends StandardDialect
    implements IExpressionEnhancingDialect
{
    private final Map<String, Object> expressionObjects;

    public ExtensionDialectImpl()
    {
        final Set<IProcessor> processors = Sets.newHashSet();
        processors.add( new ComponentProcessor() );
        setAdditionalProcessors( processors );

        this.expressionObjects = Maps.newHashMap();
        this.expressionObjects.put( "js", new JavascriptExecutor() );
    }

    @Override
    public Map<String, Object> getAdditionalExpressionObjects( final IProcessingContext context )
    {
        return this.expressionObjects;
    }

    @Override
    public String getPrefix()
    {
        return "portal";
    }
}
