package com.enonic.xp.lib.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

final class ComponentProcessor
    extends AbstractAttributeTagProcessor
{
    ComponentProcessor( final String dialectPrefix )
    {
        super( TemplateMode.HTML, dialectPrefix, null, false, "component", true, 100, true );
    }

    @Override
    protected void doProcess( final ITemplateContext context, final IProcessableElementTag tag, final AttributeName attributeName,
                              final String attributeValue, final IElementTagStructureHandler structureHandler )
    {
        structureHandler.removeAttribute( attributeName );

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser( context.getConfiguration() );

        final IStandardExpression expression = expressionParser.parseExpression( context, attributeValue );
        final Object result = expression.execute( context );

        final String componentPath = ( result == null ? "" : result.toString() );

        final IComment model = context.getModelFactory().createComment( "# COMPONENT " + componentPath + " " );
        structureHandler.setBody( model, false );
    }
}
