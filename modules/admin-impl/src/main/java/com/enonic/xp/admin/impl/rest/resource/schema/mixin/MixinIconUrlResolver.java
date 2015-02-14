package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import com.enonic.xp.admin.impl.rest.resource.schema.IconUrlResolver;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;

public final class MixinIconUrlResolver
    extends IconUrlResolver
{

    public static final String REST_SCHEMA_ICON_URL = "/admin/rest/schema/mixin/icon/";

    private final MixinIconResolver mixinIconResolver;

    public MixinIconUrlResolver( final MixinIconResolver mixinIconResolver )
    {
        this.mixinIconResolver = mixinIconResolver;
    }

    public String resolve( final Mixin mixin )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + mixin.getName().toString();
        final Icon icon = mixin.getIcon();
        return generateIconUrl( baseUrl, icon );
    }

    public String resolve( final MixinName mixinName )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + mixinName.toString();
        final Icon icon = mixinIconResolver.resolveIcon( mixinName );
        return generateIconUrl( baseUrl, icon );
    }
}
