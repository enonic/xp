package com.enonic.xp.project.layer;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;

public final class ContentLayer
{
    private final ContentLayerKey key;

    private final List<ContentLayerKey> parentKeys;

    private final String displayName;

    private final String description;

    private final Locale locale;

    private final Attachment icon;

    private ContentLayer( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "Key cannot be null" );
        Preconditions.checkNotNull( builder.displayName, "Display name cannot be null" );
        key = builder.key;
        parentKeys = builder.parentKeys.build();
        displayName = builder.displayName;
        description = builder.description;
        locale = builder.locale;
        icon = builder.icon;
    }

    public static ContentLayer from( final PropertySet layerSet )
    {
        if ( layerSet == null )
        {
            return null;
        }

        final String key = layerSet.getPropertyNames()[0];
        final PropertySet layerData = layerSet.getSet( key );

        final ContentLayer.Builder layer = ContentLayer.create();

        layer.key( ContentLayerKey.from( key ) ).
            displayName( layerData.getString( ContentLayerConstants.DISPLAY_NAME_PROPERTY ) ).
            description( layerData.getString( ContentLayerConstants.DESCRIPTION_PROPERTY ) );

        final String locale = layerData.getString( ContentLayerConstants.LOCALE_PROPERTY );

        if ( locale != null )
        {
            layer.locale( Locale.forLanguageTag( locale ) );
        }

        final Iterable<String> parentKeys = layerData.getStrings( ContentLayerConstants.PARENT_KEYS_PROPERTY );

        if ( parentKeys != null )
        {
            parentKeys.forEach( parentName -> layer.addParentKey( ContentLayerKey.from( parentName ) ) );
        }

        buildIcon( layer, layerData );

        return layer.build();

    }

    private static void buildIcon( final ContentLayer.Builder layer, final PropertySet projectData )
    {
        final PropertySet iconData = projectData.getPropertySet( ContentLayerConstants.ICON_PROPERTY );

        if ( iconData != null )
        {
            layer.icon( Attachment.create().
                name( iconData.getString( ContentPropertyNames.ATTACHMENT_NAME ) ).
                label( iconData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) ).
                mimeType( iconData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) ).
                size( iconData.getLong( ContentPropertyNames.ATTACHMENT_SIZE ) ).
                textContent( iconData.getString( ContentPropertyNames.ATTACHMENT_TEXT ) ).
                build() );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentLayerKey getKey()
    {
        return key;
    }

    public List<ContentLayerKey> getParentKeys()
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

    public Attachment getIcon()
    {
        return icon;
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
        final ContentLayer that = (ContentLayer) o;
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

        private ImmutableList.Builder<ContentLayerKey> parentKeys = ImmutableList.builder();

        private String displayName;

        private String description;

        private Locale locale;

        private Attachment icon;

        private Builder()
        {
        }

        public Builder key( final ContentLayerKey key )
        {
            this.key = key;
            return this;
        }

        public Builder addParentKey( final ContentLayerKey parentKey )
        {
            this.parentKeys.add( parentKey );
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

        public Builder icon( final Attachment icon )
        {
            this.icon = icon;
            return this;
        }

        public ContentLayer build()
        {
            return new ContentLayer( this );
        }
    }
}
