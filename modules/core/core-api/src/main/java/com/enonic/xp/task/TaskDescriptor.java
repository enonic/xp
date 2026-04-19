package com.enonic.xp.task;

import java.util.Objects;

import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;


public final class TaskDescriptor
    extends Descriptor
{
    private final String description;

    private final String descriptionI18nKey;

    private final Form config;

    private final GenericValue schemaConfig;

    private TaskDescriptor( final Builder builder )
    {
        super( builder.key );
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.config = requireNonNullElse( builder.config, Form.empty() );
        this.schemaConfig = builder.schemaConfig.build();
    }

    public String getDescription()
    {
        return description;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public Form getConfig()
    {
        return config;
    }

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final TaskDescriptor that = (TaskDescriptor) o;
        return Objects.equals( description, that.description ) && Objects.equals( descriptionI18nKey, that.descriptionI18nKey ) &&
            Objects.equals( config, that.config ) && Objects.equals( schemaConfig, that.schemaConfig );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( description, descriptionI18nKey, config, schemaConfig );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private String description;

        private String descriptionI18nKey;

        private Form config;

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder descriptionI18nKey( final String descriptionI18nKey )
        {
            this.descriptionI18nKey = descriptionI18nKey;
            return this;
        }

        public Builder description( final LocalizedText text )
        {
            this.description = text.text();
            this.descriptionI18nKey = text.i18n();
            return this;
        }

        public Builder config( final Form config )
        {
            this.config = config;
            return this;
        }

        public Builder schemaConfig( final GenericValue value )
        {
            value.properties().forEach( e -> this.schemaConfig.put( e.getKey(), e.getValue() ) );
            return this;
        }

        public TaskDescriptor build()
        {
            requireNonNull( this.key, "key is required" );
            return new TaskDescriptor( this );
        }
    }
}
