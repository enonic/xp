package com.enonic.xp.project.layer;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.data.PropertySet;

@PublicApi
public final class CreateLayerParams
{
    private final ContentLayerKey key;

    private final ContentLayerKeys parentKeys;

    private final String displayName;

    private final String description;

    private final Locale locale;

    private final CreateAttachment icon;

    private CreateLayerParams( final Builder builder )
    {
        this.key = builder.key;
        this.parentKeys = builder.parentKeys.build();
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.locale = builder.locale;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentLayerKey getKey()
    {
        return key;
    }

    public ContentLayerKeys getParentKeys()
    {
        return parentKeys;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public CreateAttachment getIcon()
    {
        return icon;
    }

    public PropertySet toData()
    {
        return ModifyLayerParams.
            create( this ).
            build().
            toData();
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
        final CreateLayerParams that = (CreateLayerParams) o;
        return Objects.equals( key, that.key ) && Objects.equals( parentKeys, that.parentKeys ) &&
            Objects.equals( displayName, that.displayName ) && Objects.equals( description, that.description ) &&
            Objects.equals( locale, that.locale ) && Objects.equals( icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key, parentKeys, displayName, description, locale, icon );
    }

    public static final class Builder
    {
        private ContentLayerKey key;

        private ContentLayerKeys.Builder parentKeys = ContentLayerKeys.create();

        private String displayName;

        private String description;

        private Locale locale;

        private CreateAttachment icon;

        private Builder()
        {
        }

        public Builder key( final ContentLayerKey key )
        {
            this.key = key;
            return this;
        }

        public Builder addParentKey( final String parentKey )
        {
            this.parentKeys.add( ContentLayerKey.from( parentKey ) );
            return this;
        }

        public Builder addParentKeys( final Collection<ContentLayerKey> parentKeys )
        {
            if ( parentKeys != null )
            {
                this.parentKeys.addAll( parentKeys );
            }
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder locale( final Locale locale )
        {
            this.locale = locale;
            return this;
        }

        public Builder icon( final CreateAttachment icon )
        {
            this.icon = icon;
            return this;
        }


        private void validate()
        {
            Preconditions.checkNotNull( key, "layer key cannot be null" );
        }

        public CreateLayerParams build()
        {
            validate();
            return new CreateLayerParams( this );
        }
    }
}
