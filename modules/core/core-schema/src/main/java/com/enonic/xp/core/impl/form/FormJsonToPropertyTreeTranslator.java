package com.enonic.xp.core.impl.form;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
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
        this.form = form != null ? form : Form.empty();
        this.strictMode = strict;
        this.propertyTree = new PropertyTree();
        this.inputTypeResolver = InputTypes.BUILTIN;
    }

    public PropertyTree translate( final JsonNode json )
    {
        for ( Map.Entry<String, JsonNode> property : json.properties() )
        {
            addValue( property.getKey(), property.getValue(), this.propertyTree.getRoot() );
        }
        return this.propertyTree;
    }

    private void addValue( final String key, final JsonNode value, final PropertySet parent )
    {
        if ( value.isArray() )
        {
            for ( final JsonNode objNode : value )
            {
                addValue( key, objNode, parent );
            }
            return;
        }

        final FormItem formItem = this.form.getFormItem( toFormItemPath( parent.getProperty(), key ) );

        if ( value.isObject() && !( formItem instanceof Input ) )
        {
            final PropertySet parentSet = parent.addSet( key );
            for ( Map.Entry<String, JsonNode> p : value.properties() )
            {
                addValue( p.getKey(), p.getValue(), parentSet );
            }
        }
        else
        {
            if ( formItem == null )
            {
                throwIfFormItemRequired( parent.getProperty(), key, value );
                parent.addProperty( key, resolveCoreValue( value ) );
            }
            else
            {
                parent.addProperty( key, resolveMappedValue( value, formItem.toInput() ) );
            }
        }
    }

    private Value resolveMappedValue( final JsonNode value, final Input input )
    {
        final InputType type = this.inputTypeResolver.resolve( input.getInputType() );
        return type.createValue( resolveCoreValue( value ), input.getInputTypeConfig() );
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

        if ( value.isBoolean() )
        {
            return ValueFactory.newBoolean( value.booleanValue() );
        }

        if ( value.isObject() )
        {
            PropertySet propertySet = propertyTree.newSet();
            for ( Map.Entry<String, JsonNode> field : value.properties() )
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
            }
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

    private FormItemPath toFormItemPath( final Property parentProperty, final String key )
    {
        return parentProperty == null ? FormItemPath.from( key ) : FormItemPath.from( FormItemPath.from( parentProperty.getPath() ), key );
    }

    private void throwIfFormItemRequired( final Property parentProperty, final String key, final JsonNode value )
    {
        if ( this.strictMode )
        {
            final boolean isOptionSetSelection = OPTION_SET_SELECTION_ARRAY_NAME.equals( key ) && this.form.getFormItem(
                toFormItemPath( parentProperty.getParent().getProperty(), parentProperty.getName() ) ) instanceof FormOptionSet;
            if ( !isOptionSetSelection )
            {
                throw new IllegalArgumentException(
                    "No mapping defined for property " + key + " with value " + resolveStringValue( value ) );
            }
        }
    }
}
