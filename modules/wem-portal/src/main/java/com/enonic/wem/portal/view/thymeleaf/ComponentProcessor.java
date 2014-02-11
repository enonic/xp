package com.enonic.wem.portal.view.thymeleaf;

import java.util.List;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.attr.AbstractFragmentHandlingAttrProcessor;

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
        final List<Node> list = Lists.newArrayList();
        final Comment comment = new Comment( "# COMPONENT " + attributeValue + " " );
        list.add( comment );
        return list;
    }
}
