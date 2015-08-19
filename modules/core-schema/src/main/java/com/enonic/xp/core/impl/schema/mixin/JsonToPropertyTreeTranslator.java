package com.enonic.xp.core.impl.schema.mixin;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputType;
import com.enonic.xp.inputtype.InputTypeResolver;
import com.enonic.xp.inputtype.InputTypes;

public class JsonToPropertyTreeTranslator
{
    private final FormItems formItems;

    private final PropertyTree propertyTree;

    private final InputTypeResolver inputTypeResolver;

    private final Mode mode;

    private JsonToPropertyTreeTranslator( final Builder builder )
    {
        this.formItems = builder.formItems != null ? builder.formItems : Form.create().build().getFormItems();
        this.mode = builder.mode;
        this.propertyTree = new PropertyTree();
        this.inputTypeResolver = InputTypes.BUILTIN;
    }

    public PropertyTree translate( final JsonNode json )
    {
        traverse( json, propertyTree.getRoot() );

        return propertyTree;
    }

    private void traverse( final JsonNode json, final PropertySet parent )
    {
        final Iterator<Map.Entry<String, JsonNode>> fields = json.fields();

        while ( fields.hasNext() )
        {
            final Map.Entry<String, JsonNode> next = fields.next();

            if ( next.getValue().isObject() )
            {
                final PropertySet propertySet = parent.addSet( next.getKey() );
                traverse( next.getValue(), propertySet );
            }
            else
            {
                addValue( parent, next.getKey(), next.getValue() );
            }
        }
    }

    private void addValue( final PropertySet parent, final String key, final JsonNode value )
    {
        if ( value.isArray() )
        {
            for ( final JsonNode objNode : value )
            {
                addValue( parent, key, objNode );
            }
        }
        else if ( value.isObject() )
        {
            final PropertySet parentSet = parent.addSet( key );
            value.fields().forEachRemaining( ( objectValue ) -> addValue( parentSet, objectValue.getKey(), objectValue.getValue() ) );
        }
        else
        {
            mapValue( parent, key, value );
        }
    }

    private void mapValue( final PropertySet parent, final String key, final JsonNode value )
    {
        final Property parentProperty = parent.getProperty();

        final Input input = this.formItems.getInput( resolveInputPath( key, parentProperty ) );

        if ( input == null )
        {
            if ( this.mode.equals( Mode.STRICT ) )
            {
                throw new IllegalArgumentException(
                    "No mapping defined for property " + key + " with value " + resolveStringValue( value ) );
            }

            parent.addProperty( key, resolveCoreValue( value ) );
        }
        else
        {
            final InputType type = this.inputTypeResolver.resolve( input.getInputType() );
            final Value mappedPropertyValue = type.createValue( resolveStringValue( value ), input.getInputTypeConfig() );

            parent.addProperty( key, mappedPropertyValue );
        }
    }

    private Value resolveCoreValue( final JsonNode value )
    {
        if ( value.isTextual() )
        {
            return ValueFactory.newString( value.textValue() );
        }

        if ( value.isDouble() )
        {
            return ValueFactory.newDouble( value.doubleValue() );
        }

        if ( value.isInt() )
        {
            return ValueFactory.newLong( (long) value.intValue() );
        }

        if ( value.isLong() )
        {
            return ValueFactory.newLong( value.longValue() );
        }

        return ValueFactory.newString( value.toString() );
    }

    private String resolveStringValue( final JsonNode value )
    {
        if ( value.isTextual() )
        {
            return value.textValue();
        }

        return value.toString();
    }

    private FormItemPath resolveInputPath( final String key, final Property parentProperty )
    {
        if ( parentProperty == null )
        {
            return FormItemPath.from( key );
        }

        final FormItemPath parentPath = FormItemPath.from( parentProperty.getPath().resetAllIndexesTo( 0 ).toString() );
        return FormItemPath.from( FormItemPath.from( parentPath, key ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private FormItems formItems;

        private Mode mode = Mode.STRICT;

        public Builder formItems( final FormItems formItems )
        {
            this.formItems = formItems;
            return this;
        }

        public Builder mode( final Mode mode )
        {
            this.mode = mode;
            return this;
        }

        public JsonToPropertyTreeTranslator build()
        {
            return new JsonToPropertyTreeTranslator( this );
        }
    }

    public enum Mode
    {
        STRICT,
        LENIENT
    }
}
