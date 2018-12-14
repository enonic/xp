package com.enonic.xp.admin.impl.rest.resource.macro.json;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.macro.MacroDescriptor;

public class MacroDescriptorJson
{
    private String key;

    private String name;

    private String displayName;

    private String description;

    private FormJson form;

    private String iconUrl;

    private String displayNameI18nKey;

    private final LocaleMessageResolver localeMessageResolver;

    private String descriptionI18nKey;

    public MacroDescriptorJson( final Builder builder )
    {
        Preconditions.checkNotNull( builder.localeMessageResolver );
        Preconditions.checkNotNull( builder.macroIconUrlResolver );
        Preconditions.checkNotNull( builder.macroDescriptor );

        this.localeMessageResolver = builder.localeMessageResolver;

        this.key = builder.macroDescriptor.getKey().toString();
        this.name = builder.macroDescriptor.getName();
        this.displayName = builder.macroDescriptor.getDisplayName();
        this.displayNameI18nKey = builder.macroDescriptor.getDisplayNameI18nKey();
        this.descriptionI18nKey = builder.macroDescriptor.getDescriptionI18nKey();
        this.description = builder.macroDescriptor.getDescription();
        this.form = new FormJson( builder.macroDescriptor.getForm(), builder.localeMessageResolver, builder.inlineMixinResolver );
        this.iconUrl = builder.macroIconUrlResolver.resolve( builder.macroDescriptor );
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        if ( StringUtils.isNotBlank( displayNameI18nKey ) )
        {
            return localeMessageResolver.localizeMessage( displayNameI18nKey, displayName );
        }
        else
        {
            return displayName;
        }
    }

    public String getDescription()
    {
        if ( StringUtils.isNotBlank( descriptionI18nKey ) )
        {
            return localeMessageResolver.localizeMessage( descriptionI18nKey, description );
        }
        else
        {
            return description;
        }
    }

    public FormJson getForm()
    {
        return form;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private MacroDescriptor macroDescriptor;

        private MacroIconUrlResolver macroIconUrlResolver;

        private LocaleMessageResolver localeMessageResolver;

        private InlineMixinResolver inlineMixinResolver;

        public Builder setMacroDescriptor( final MacroDescriptor macroDescriptor )
        {
            this.macroDescriptor = macroDescriptor;
            return this;
        }

        public Builder setMacroIconUrlResolver( final MacroIconUrlResolver macroIconUrlResolver )
        {
            this.macroIconUrlResolver = macroIconUrlResolver;
            return this;
        }

        public Builder setLocaleMessageResolver( final LocaleMessageResolver localeMessageResolver )
        {
            this.localeMessageResolver = localeMessageResolver;
            return this;
        }

        public Builder setInlineMixinResolver( final InlineMixinResolver inlineMixinResolver )
        {
            this.inlineMixinResolver = inlineMixinResolver;
            return this;
        }

        public MacroDescriptorJson build()
        {
            return new MacroDescriptorJson( this );
        }
    }
}
