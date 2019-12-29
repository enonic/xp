package com.enonic.xp.macro;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.form.Form;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public final class MacroDescriptor
{
    private final static String SITE_MACROS_PREFIX = "site/macros/";

    private final MacroKey key;

    private final String displayName;

    private final String displayNameI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final Form form;

    private final Icon icon;

    private MacroDescriptor( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName == null ? builder.key.getName() : builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.form = builder.form == null ? Form.create().build() : builder.form;
        this.icon = builder.icon;
    }

    public MacroKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return key.getName();
    }

    public String getDisplayName()
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

    public Form getForm()
    {
        return form;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public ResourceKey toDescriptorResourceKey()
    {
        return ResourceKey.from( key.getApplicationKey(), SITE_MACROS_PREFIX + key.getName() + "/" + key.getName() + ".xml" );
    }

    public ResourceKey toControllerResourceKey()
    {
        return ResourceKey.from( key.getApplicationKey(), SITE_MACROS_PREFIX + key.getName() + "/" + key.getName() + ".js" );
    }

    public static ResourceKey toDescriptorResourceKey( final MacroKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), SITE_MACROS_PREFIX + key.getName() + "/" + key.getName() + ".xml" );
    }

    public static ResourceKey toControllerResourceKey( final MacroKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), SITE_MACROS_PREFIX + key.getName() + "/" + key.getName() + ".js" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private MacroKey key;

        private String displayName;

        private String displayNameI18nKey;

        private String description;

        private String descriptionI18nKey;

        private Form form;

        private Icon icon;

        private Builder()
        {
        }

        public Builder key( final MacroKey key )
        {
            this.key = key;
            return this;
        }

        public Builder key( final String key )
        {
            this.key = MacroKey.from( key );
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder displayNameI18nKey( final String displayNameI18nKey )
        {
            this.displayNameI18nKey = displayNameI18nKey;
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

        public Builder form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder icon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public MacroDescriptor build()
        {
            return new MacroDescriptor( this );
        }
    }

}
