package com.enonic.xp.core.impl.content;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;

public class JsonToPropertyTreeTranslator
{
    private final FormItems formItems;

    private final PropertyTree propertyTree;

    private final Mode mode;

    private JsonToPropertyTreeTranslator( final Builder builder )
    {
        this.formItems = builder.form != null ? builder.form.getFormItems() : Form.newForm().build().getFormItems();
        this.mode = builder.mode;
        this.propertyTree = new PropertyTree();
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
                final PropertySet propertySet = this.propertyTree.addSet( next.getKey() );
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
            final Value mappedPropertyValue =
                input.getInputType().createPropertyValue( resolveStringValue( value ), input.getInputTypeConfig() );

            parent.addProperty( key, mappedPropertyValue );
        }
    }

    private Value resolveCoreValue( final JsonNode value )
    {
        if ( value.isTextual() )
        {
            return Value.newString( value.textValue() );
        }

        if ( value.isDouble() )
        {
            return Value.newDouble( value.doubleValue() );
        }

        if ( value.isInt() )
        {
            return Value.newDouble( (double) value.intValue() );
        }

        if ( value.isLong() )
        {
            return Value.newLong( value.longValue() );
        }

        return Value.newString( value.toString() );
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

        final FormItemPath parentPath = FormItemPath.from( parentProperty.getPath().toString() );
        return FormItemPath.from( FormItemPath.from( parentPath, key ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Form form;

        private Mode mode = Mode.STRICT;

        public Builder form( final Form form )
        {
            this.form = form;
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
