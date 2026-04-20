package com.enonic.xp.macro;

import java.time.Instant;
import com.enonic.xp.form.Form;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNullElse;


public final class MacroDescriptor
{
    private final MacroKey key;

    private final String title;

    private final String titleI18nKey;

    private final String description;

    private final String descriptionI18nKey;

    private final Form form;

    private final Icon icon;

    private final Instant modifiedTime;

    private final GenericValue schemaConfig;

    private MacroDescriptor( final Builder builder )
    {
        this.key = builder.key;
        this.title = builder.title == null ? builder.key.getName() : builder.title;
        this.titleI18nKey = builder.titleI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.form = requireNonNullElse( builder.form, Form.empty() );
        this.icon = builder.icon;
        this.modifiedTime = builder.modifiedTime;
        this.schemaConfig = builder.schemaConfig.build();
    }

    public MacroKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return key.getName();
    }

    public String getTitle()
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

    public Form getForm()
    {
        return form;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public GenericValue getSchemaConfig()
    {
        return schemaConfig;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private MacroKey key;

        private String title;

        private String titleI18nKey;

        private String description;

        private String descriptionI18nKey;

        private Form form;

        private Icon icon;

        private Instant modifiedTime;

        private final GenericValue.ObjectBuilder schemaConfig = GenericValue.newObject();

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

        public Builder title( final String title )
        {
            this.title = title;
            return this;
        }

        public Builder titleI18nKey( final String titleI18nKey )
        {
            this.titleI18nKey = titleI18nKey;
            return this;
        }

        public Builder title( final LocalizedText text )
        {
            this.title = text.text();
            this.titleI18nKey = text.i18n();
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

        public Builder modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder schemaConfig( final GenericValue value )
        {
            value.properties().forEach( e -> this.schemaConfig.put( e.getKey(), e.getValue() ) );
            return this;
        }

        public MacroDescriptor build()
        {
            return new MacroDescriptor( this );
        }
    }

}
