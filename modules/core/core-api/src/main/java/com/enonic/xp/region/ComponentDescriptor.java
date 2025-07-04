package com.enonic.xp.region;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public abstract class ComponentDescriptor
    extends Descriptor
{
    private final String displayName;

    private final String displayNameI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    protected final Instant modifiedTime;

    private final Form config;

    private final InputTypeConfig schemaConfig;

    protected ComponentDescriptor( final BaseBuilder builder )
    {
        super( builder.key );

        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        this.displayName = builder.displayName == null || builder.displayName.isBlank() ? builder.name : builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.modifiedTime = builder.modifiedTime;
        this.config = builder.config;
        this.schemaConfig = builder.schemaConfig.build();
    }

    public final String getDisplayName()
    {
        return displayName;
    }

    public String getDisplayNameI18nKey()
    {
        return displayNameI18nKey;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public final Form getConfig()
    {
        return config;
    }

    public InputTypeConfig getSchemaConfig()
    {
        return schemaConfig;
    }

    public abstract ResourceKey getComponentPath();

    public abstract static class BaseBuilder<T extends BaseBuilder>
    {
        protected DescriptorKey key;

        protected String name;

        protected String displayName;

        protected String displayNameI18nKey;

        protected String description;

        protected String descriptionI18nKey;

        protected Instant modifiedTime;

        protected Form config;

        private final InputTypeConfig.Builder schemaConfig = InputTypeConfig.create();

        protected BaseBuilder()
        {
        }

        protected BaseBuilder( final ComponentDescriptor descriptor )
        {
            this.key = descriptor.getKey();
            this.name = descriptor.getName();
            this.displayName = descriptor.getDisplayName();
            this.displayNameI18nKey = descriptor.getDisplayNameI18nKey();
            this.description = descriptor.getDescription();
            this.descriptionI18nKey = descriptor.getDescriptionI18nKey();
            this.modifiedTime = descriptor.getModifiedTime();
            this.config = descriptor.getConfig();
            if ( descriptor.schemaConfig != null )
            {
                this.schemaConfig.config( descriptor.schemaConfig );
            }
        }

        public final T key( final DescriptorKey key )
        {
            this.key = key;
            return typecastToBuilder( this );
        }

        public final T displayName( final String displayName )
        {
            this.displayName = displayName;
            return typecastToBuilder( this );
        }

        public final T displayNameI18nKey( final String displayNameI18nKey )
        {
            this.displayNameI18nKey = displayNameI18nKey;
            return typecastToBuilder( this );
        }

        public final T description( String description )
        {
            this.description = description;
            return typecastToBuilder( this );
        }

        public final T descriptionI18nKey( final String descriptionI18nKey )
        {
            this.descriptionI18nKey = descriptionI18nKey;
            return typecastToBuilder( this );
        }

        public final T modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return typecastToBuilder( this );
        }

        public final T config( final Form value )
        {
            this.config = value;
            return typecastToBuilder( this );
        }

        public final T schemaConfig( final InputTypeConfig value )
        {
            this.schemaConfig.config( value );
            return typecastToBuilder( this );
        }

        @SuppressWarnings("unchecked")
        private T typecastToBuilder( final BaseBuilder object )
        {
            return (T) object;
        }
    }
}
