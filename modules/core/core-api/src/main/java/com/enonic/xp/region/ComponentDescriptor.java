package com.enonic.xp.region;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public abstract class ComponentDescriptor
    extends Descriptor
{
    private final String displayName;

    private final String displayNameI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final Form config;

    protected ComponentDescriptor( final BaseBuilder builder )
    {
        super( builder.key );

        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        this.displayName = builder.displayName == null || builder.displayName.trim().isEmpty() ? builder.name : builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.config = builder.config;
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

    public final Form getConfig()
    {
        return config;
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

        protected Form config;

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
            this.config = descriptor.getConfig();
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

        public final T config( final Form value )
        {
            this.config = value;
            return typecastToBuilder( this );
        }

        @SuppressWarnings("unchecked")
        private T typecastToBuilder( final BaseBuilder object )
        {
            return (T) object;
        }
    }
}
