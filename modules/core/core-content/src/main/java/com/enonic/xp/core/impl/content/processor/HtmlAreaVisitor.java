package com.enonic.xp.core.impl.content.processor;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.core.impl.content.InputVisitorHelper;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.inputtype.InputTypeName;

public class HtmlAreaVisitor
    extends InputVisitor
{
    private final PropertyTree propertyTree;

    private final ImmutableSet.Builder<Property> properties = ImmutableSet.builder();

    public HtmlAreaVisitor( final PropertyTree propertyTree )
    {
        this.propertyTree = propertyTree;
    }

    @Override
    public void visit( final Input input )
    {
        if ( InputTypeName.HTML_AREA.equals( input.getInputType() ) )
        {
            InputVisitorHelper.visitProperties( input, propertyTree, properties::add );
        }

    }

    public Set<Property> getProperties()
    {
        return properties.build();
    }
}
