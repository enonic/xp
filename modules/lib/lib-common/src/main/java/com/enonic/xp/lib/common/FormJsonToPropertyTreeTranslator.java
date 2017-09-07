package com.enonic.xp.lib.common;

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
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputType;
import com.enonic.xp.inputtype.InputTypeResolver;
import com.enonic.xp.inputtype.InputTypes;

public final class FormJsonToPropertyTreeTranslator
{
    private final Form form;

    private final PropertyTree propertyTree;

    private final InputTypeResolver inputTypeResolver;

    private final boolean strictMode;

    private static final String OPTION_SET_SELECTION_ARRAY_NAME = "_selected";

    public FormJsonToPropertyTreeTranslator( final Form form, final boolean strict )
    {
        this.form = form != null ? form : Form.create().build();
        this.strictMode = strict;
        this.propertyTree = new PropertyTree();
        this.inputTypeResolver = InputTypes.BUILTIN;
    }

    public PropertyTree translate( final JsonNode json )
    {
        traverse( json, this.propertyTree.getRoot() );
        return this.propertyTree;
    }

    private void traverse( final JsonNode json, final PropertySet parent )
    {
        final Iterator<Map.Entry<String, JsonNode>> fields = json.fields();

        while ( fields.hasNext() )
        {
            final Map.Entry<String, JsonNode> next = fields.next();
            addValue( parent, next.getKey(), next.getValue() );
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
        else if ( value.isObject() && !hasInput( parent.getProperty(), key ) )
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
        final Input input = getInput( parentProperty, key );

        if ( input == null )
        {
            if ( this.strictMode && !isOptionSetSelection( key, parentProperty ) )
            {
                throw new IllegalArgumentException(
                    "No mapping defined for property " + key + " with value " + resolveStringValue( value ) );
            }

            parent.addProperty( key, resolveCoreValue( value ) );
        }
        else
        {
            final InputType type = this.inputTypeResolver.resolve( input.getInputType() );
            final Value mappedPropertyValue = type.createValue( resolveCoreValue( value ), input.getInputTypeConfig() );

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

        if ( value.isObject() )
        {
            PropertySet propertySet = new PropertySet();
            value.fields().
                forEachRemaining( ( field ) ->
                                  {
                                      if ( field.getValue().isArray() )
                                      {
                                          for ( final JsonNode arrayNode : field.getValue() )
                                          {
                                              propertySet.addProperty( field.getKey(), resolveCoreValue( arrayNode ) );
                                          }
                                      }
                                      else
                                      {
                                          propertySet.addProperty( field.getKey(), resolveCoreValue( field.getValue() ) );
                                      }
                                  } );
            return ValueFactory.newPropertySet( propertySet );
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

    private boolean hasInput( final Property parentProperty, final String key )
    {
        return Input.class.isInstance( this.form.getFormItem( resolveInputPath( key, parentProperty ) ) );
    }

    private Input getInput( final Property parentProperty, final String key )
    {
        return this.form.getInput( resolveInputPath( key, parentProperty ) );
    }

    private boolean isOptionSetSelection( final String key, final Property parentProperty )
    {
        return OPTION_SET_SELECTION_ARRAY_NAME.equals( key ) && FormOptionSet.class.isInstance(
            this.form.getFormItem( resolveInputPath( parentProperty.getName(), parentProperty.getParent().getProperty() ) ) );
    }

}
