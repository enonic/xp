package com.enonic.xp.admin.ui.tool;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.ui.tool.mapper.AdminToolMapper;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKeys;

public class GetAdminToolsScriptBean
    implements ScriptBean
{

    private AdminToolDescriptorService adminToolDescriptorService;

    public List<AdminToolMapper> execute()
    {
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        return adminToolDescriptorService.getAllowedAdminToolDescriptors( principals ).
            stream().
            filter( AdminToolDescriptor::isAppLauncherApplication ).
            map( adminToolDescriptor -> new AdminToolMapper( adminToolDescriptor ) ).
            collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.adminToolDescriptorService = context.getService( AdminToolDescriptorService.class ).get();
    }
}
