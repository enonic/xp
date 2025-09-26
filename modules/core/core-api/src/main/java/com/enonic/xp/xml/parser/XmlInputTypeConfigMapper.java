package com.enonic.xp.xml.parser;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.w3c.dom.Attr;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.StringPropertyValue;
import com.enonic.xp.xml.DomElement;

import static com.google.common.base.Strings.isNullOrEmpty;

final class XmlInputTypeConfigMapper
{
    private final Function<String, String> nameResolver;

    private final BiFunction<String, String, String> valueResolver;

    XmlInputTypeConfigMapper( final ApplicationKey currentApplication, final InputTypeName inputTypeName )
    {
        final Function<String, String> aliasNamesFunction = InputTypeAliasConverters.getConverter( inputTypeName )::convert;
        final Function<String, String> hyphenToCameNameFunction = XmlInputTypeDefaultMapper::resolveName;

        this.nameResolver = aliasNamesFunction.andThen( hyphenToCameNameFunction );
        this.valueResolver = new DefaultResolveValue( currentApplication );
    }

    XmlInputTypeConfigMapper()
    {
        this.nameResolver = XmlInputTypeDefaultMapper::resolveName;
        this.valueResolver = ( n, v ) -> v;
    }

    public InputTypeConfig build( final DomElement root )
    {
        final InputTypeConfig.Builder builder = InputTypeConfig.create();

        if ( root != null )
        {
            build( builder, root );
        }

        return builder.build();
    }

    private void build( final InputTypeConfig.Builder builder, final DomElement root )
    {
        for ( final DomElement child : root.getChildren() )
        {
            builder.property( buildProperty( child ) );
        }
    }

    private InputTypeProperty buildProperty( final DomElement root )
    {
        final String name = nameResolver.apply( root.getTagName() );
        final String value = valueResolver.apply( name, root.getValue() );

        final InputTypeProperty.Builder builder = InputTypeProperty.create( name, new StringPropertyValue( value ) );
        for ( final Attr attr : root.getAttributes() )
        {
            addPropertyAttribute( builder, attr );
        }

        return builder.build();
    }

    private void addPropertyAttribute( final InputTypeProperty.Builder builder, final Attr attr )
    {
        final String name = nameResolver.apply( attr.getName() );
        final String value = valueResolver.apply( name, attr.getValue() );
//        builder.attribute( name, value );
    }

    private static class DefaultResolveValue
        implements BiFunction<String, String, String>
    {
        final ApplicationRelativeResolver relativeResolver;

        DefaultResolveValue( final ApplicationKey applicationKey )
        {
            this.relativeResolver = new ApplicationRelativeResolver( applicationKey );
        }

        @Override
        public String apply( final String name, final String value )
        {
            if ( isNullOrEmpty( value ) )
            {
                return null;
            }
            final String lowerCasedName = name.toLowerCase( Locale.ROOT );
            if ( lowerCasedName.endsWith( "service" ) )
            {
                return relativeResolver.toServiceUrl( value );
            }
            else if ( lowerCasedName.endsWith( "mixintype" ) )
            {
                return relativeResolver.toMixinName( value ).toString();
            }
            else
            {
                return value;
            }
        }
    }
}
