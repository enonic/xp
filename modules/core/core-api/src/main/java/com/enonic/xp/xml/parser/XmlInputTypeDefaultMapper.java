package com.enonic.xp.xml.parser;

import org.w3c.dom.Attr;

import com.google.common.base.CaseFormat;

import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.xml.DomElement;

final class XmlInputTypeDefaultMapper
{

    public InputTypeDefault build( final DomElement root )
    {
        final InputTypeDefault.Builder builder = InputTypeDefault.create();

        if ( root != null )
        {
            build( builder, root );
        }

        return builder.build();
    }

    private void build( final InputTypeDefault.Builder builder, final DomElement root )
    {
        builder.property(
            InputTypeProperty.create( root.getTagName(), root.getChildren().size() > 0 ? root.serializeBody() : root.getValue() ).build() );

        for ( final DomElement child : root.getChildren() )
        {
            builder.property( buildProperty( child ) );
        }
    }

    private InputTypeProperty buildProperty( final DomElement root )
    {
        final String name = resolveName( root.getTagName() );

        final InputTypeProperty.Builder builder = InputTypeProperty.create( name, root.getValue() );
        for ( final Attr attr : root.getAttributes() )
        {
            addPropertyAttribute( builder, attr );
        }

        return builder.build();
    }

    private void addPropertyAttribute( final InputTypeProperty.Builder builder, final Attr attr )
    {
        final String name = resolveName( attr.getName() );
        builder.attribute( name, attr.getValue() );
    }

    private String resolveName( final String name )
    {
        if ( name.contains( "-" ) )
        {
            return CaseFormat.LOWER_HYPHEN.to( CaseFormat.LOWER_CAMEL, name );
        }

        return name;
    }


}
