package com.enonic.xp.admin.impl.rest.resource.tool.json;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.tool.AdminToolDescriptor;

public class AdminToolJson
{
    private final LocaleMessageResolver localeMessageResolver;

    private final AdminToolDescriptor adminToolDescriptor;

    private final AdminToolKeyJson key;

    public AdminToolJson( final AdminToolDescriptor adminToolDescriptor, final LocaleMessageResolver localeMessageResolver )
    {
        this.adminToolDescriptor = adminToolDescriptor;
        this.localeMessageResolver = localeMessageResolver;

        this.key = new AdminToolKeyJson( adminToolDescriptor.getKey() );
    }

    public AdminToolKeyJson getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        if ( StringUtils.isNotBlank( adminToolDescriptor.getDisplayNameI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( adminToolDescriptor.getDisplayNameI18nKey(),
                                                          adminToolDescriptor.getDisplayName() );
        }
        else
        {
            return adminToolDescriptor.getDisplayName();
        }
    }

    public String getDescription()
    {
        if ( StringUtils.isNotBlank( adminToolDescriptor.getDescriptionI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( adminToolDescriptor.getDescriptionI18nKey(),
                                                          adminToolDescriptor.getDescription() );
        }
        else
        {
            return adminToolDescriptor.getDescription();
        }
    }
}
