package com.enonic.xp.region;

import java.time.Instant;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNullElse;


public abstract class ComponentDescriptor
    extends Descriptor
{
    private final String title;

    private final String titleI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    protected final Instant modifiedTime;

    private final Form config;

    private final GenericValue schemaConfig;

    protected ComponentDescriptor( final BaseBuilder builder )
    {
        super( builder.key );

        this.title = builder.title == null || builder.title.isBlank() ? builder.name : builder.title;
        this.titleI18nKey = builder.titleI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.modifiedTime = builder.modifiedTime;
        this.config = requireNonNullElse( builder.config, Form.empty() );
        this.schemaConfig = builder.schemaConfig.build();
    }

    public final String getTitle()
    {
        return title;
    }

    public String getTitleI18nKey()
    {
        return titleI18nKey;
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

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
    }

    public abstract ResourceKey getComponentPath();

    public abstract static class BaseBuilder<T extends BaseBuilder>
    {
        protected DescriptorKey key;

        protected String name;

        protected String title;

        protected String titleI18nKey;

        protected String description;

        protected String descriptionI18nKey;

        protected Instant modifiedTime;

        protected Form config;

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

        protected BaseBuilder()
        {
        }

        protected BaseBuilder( final ComponentDescriptor descriptor )
        {
            this.key = descriptor.getKey();
            this.name = descriptor.getName();
            this.title = descriptor.getTitle();
            this.titleI18nKey = descriptor.getTitleI18nKey();
            this.description = descriptor.getDescription();
            this.descriptionI18nKey = descriptor.getDescriptionI18nKey();
            this.modifiedTime = descriptor.getModifiedTime();
            this.config = descriptor.getConfig();

            if ( descriptor.schemaConfig != null )
            {
                descriptor.schemaConfig.properties().forEach( p -> this.schemaConfig.put( p.getKey(), p.getValue() ) );
            }
        }

        public final T key( final DescriptorKey key )
        {
            this.key = key;
            return typecastToBuilder( this );
        }

        public final T title( final String title )
        {
            this.title = title;
            return typecastToBuilder( this );
        }

        public final T titleI18nKey( final String titleI18nKey )
        {
            this.titleI18nKey = titleI18nKey;
            return typecastToBuilder( this );
        }

        public final T title( final LocalizedText text )
        {
            this.title = text.text();
            this.titleI18nKey = text.i18n();
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

        public final T description( final LocalizedText text )
        {
            this.description = text.text();
            this.descriptionI18nKey = text.i18n();
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

        public final T schemaConfig( final GenericValue config )
        {
            config.properties().forEach( e -> this.schemaConfig.put( e.getKey(), e.getValue() ) );
            return typecastToBuilder( this );
        }

        @SuppressWarnings("unchecked")
        private T typecastToBuilder( final BaseBuilder object )
        {
            return (T) object;
        }
    }
}
