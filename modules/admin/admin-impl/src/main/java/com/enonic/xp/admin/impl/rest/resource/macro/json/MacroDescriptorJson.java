package com.enonic.xp.admin.impl.rest.resource.macro.json;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
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

    public MacroDescriptorJson( final MacroDescriptor macroDescriptor, final MacroIconUrlResolver macroIconUrlResolver,
                                final LocaleMessageResolver localeMessageResolver )
    {
        Preconditions.checkNotNull( localeMessageResolver );
        this.localeMessageResolver = localeMessageResolver;

        this.key = macroDescriptor.getKey().toString();
        this.name = macroDescriptor.getName();
        this.displayName = macroDescriptor.getDisplayName();
        this.displayNameI18nKey = macroDescriptor.getDisplayNameI18nKey();
        this.descriptionI18nKey = macroDescriptor.getDescriptionI18nKey();
        this.description = macroDescriptor.getDescription();
        this.form = new FormJson( macroDescriptor.getForm(), localeMessageResolver );
        this.iconUrl = macroIconUrlResolver.resolve( macroDescriptor );
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
}
