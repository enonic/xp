package com.enonic.xp.portal.impl.thymeleaf;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.attr.AbstractFragmentHandlingAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import com.google.common.collect.Lists;

final class ComponentProcessor
    extends AbstractFragmentHandlingAttrProcessor
{
    public ComponentProcessor()
    {
        super( "component" );
    }

    @Override
    public int getPrecedence()
    {
        return 100;
    }

    @Override
    protected boolean getRemoveHostNode( final Arguments arguments, final Element element, final String attributeName,
                                         final String attributeValue )
    {
        return false;
    }

    @Override
    protected List<Node> computeFragment( final Arguments arguments, final Element element, final String attributeName,
                                          final String attributeValue )
    {
        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser( configuration );

        final IStandardExpression expression = expressionParser.parseExpression( configuration, arguments, attributeValue );
        final Object result = expression.execute( configuration, arguments );

        final String componentPath = ( result == null ? "" : result.toString() );

        final List<Node> list = Lists.newArrayList();
        final Comment comment = new Comment( "# COMPONENT " + componentPath + " " );
        list.add( comment );
        return list;
    }
}
