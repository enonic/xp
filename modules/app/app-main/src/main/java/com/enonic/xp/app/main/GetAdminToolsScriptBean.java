package com.enonic.xp.app.main;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKeys;

public final class GetAdminToolsScriptBean
    implements ScriptBean
{
    private AdminToolDescriptorService adminToolDescriptorService;

    public List<MapSerializable> execute()
    {
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        return adminToolDescriptorService.getAllowedAdminToolDescriptors( principals ).
            stream().
            filter( AdminToolDescriptor::isAppLauncherApplication ).
            map( adminToolDescriptor -> new AdminToolMapper( adminToolDescriptor,
                                                             adminToolDescriptorService.getIconByKey( adminToolDescriptor.getKey() ) ) ).
            collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.adminToolDescriptorService = context.getService( AdminToolDescriptorService.class ).get();
    }
}
