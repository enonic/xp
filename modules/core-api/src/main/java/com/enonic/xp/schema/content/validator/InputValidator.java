package com.enonic.xp.schema.content.validator;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyVisitor;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InvalidDataException;
import com.enonic.xp.schema.content.ContentType;


public final class InputValidator
{
    private final PropertyValidationVisitor propertyValidationVisitor;

    private InputValidator( final ContentType contentType, final boolean requireMappedProperties )
    {
        if ( contentType.getName().isUnstructured() )
        {
            this.propertyValidationVisitor = null;
        }
        else
        {
            this.propertyValidationVisitor = new PropertyValidationVisitor( contentType.form(), requireMappedProperties );
        }
    }

    public final void validate( final PropertySet dataSet )
    {
        if ( propertyValidationVisitor != null )
        {
            propertyValidationVisitor.traverse( dataSet );
        }
    }

    public static Builder newInputValidator()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentType contentType;

        private boolean requireMappedProperties;

        public Builder contentType( final ContentType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder requireMappedProperties( final boolean requireMappedProperties )
        {
            this.requireMappedProperties = requireMappedProperties;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( contentType, "No content type given" );
        }

        public InputValidator build()
        {
            this.validate();
            return new InputValidator( this.contentType, this.requireMappedProperties );
        }
    }

    private class PropertyValidationVisitor
        extends PropertyVisitor
    {
        private final Form form;

        private final boolean requireMappedProperties;

        public PropertyValidationVisitor( final Form form, final boolean requireMappedProperties )
        {
            this.form = form;
            this.requireMappedProperties = requireMappedProperties;
        }

        @Override
        public void visit( final Property property )
        {
            FormItem formItem = form.getFormItem( property.getPath().toString() );

            if ( formItem == null )
            {
                if ( requireMappedProperties )
                {
                    throw new InvalidDataException( property, "No FormItem found for this property" );
                }
            }
            else
            {
                if ( formItem instanceof Input )
                {
                    ( (Input) formItem ).checkValidity( property );
                }
                else
                {
                    throw new InvalidDataException( property, "The FormItem found for this property is not an Input" );
                }
            }
        }
    }
}

