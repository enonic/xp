package com.enonic.xp.lib.admin;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

public final class AdminLibHelper
    implements ScriptBean
{
    private Supplier<PortalRequest> requestSupplier;

    public AdminLibHelper()
    {
    }

    public List<String> getLocales()
    {
        final PortalRequest portalRequest = requestSupplier.get();
        return ( portalRequest != null ? portalRequest.getLocales() : Collections.singletonList( Locale.getDefault() ) ).stream()
            .map( Locale::toLanguageTag )
            .toList();
    }

    public String getInstallation()
    {
        return ServerInfo.get().getName();
    }

    public String getVersion()
    {
        return VersionInfo.get().getVersion();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.requestSupplier = context.getBinding( PortalRequest.class );
    }
}
