package com.enonic.xp.admin.impl.rest.resource.macro;

import com.enonic.xp.admin.impl.rest.resource.schema.IconUrlResolver;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;

public class MacroIconUrlResolver
    extends IconUrlResolver
{
    public static final String REST_SCHEMA_ICON_URL = "/admin/rest/macro/icon/";

    private final MacroIconResolver macroIconResolver;

    public MacroIconUrlResolver( final MacroIconResolver macroIconResolver )
    {
        this.macroIconResolver = macroIconResolver;
    }

    public String resolve( final MacroDescriptor macroDescriptor )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + macroDescriptor.getKey().toString();
        final Icon icon = macroDescriptor.getIcon();
        return generateIconUrl( baseUrl, icon );
    }

    public String resolve( final MacroKey macroKey )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + macroKey.toString();
        final Icon icon = macroIconResolver.resolveIcon( macroKey );
        return generateIconUrl( baseUrl, icon );
    }
}
