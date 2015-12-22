package com.enonic.xp.admin.ui.adminapp;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptorService;
import com.enonic.xp.admin.ui.adminapp.mapper.AdminApplicationMapper;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKeys;

public class GetAdminApplicationsScriptBean
    implements ScriptBean
{

    private AdminApplicationDescriptorService adminApplicationDescriptorService;

    public List<AdminApplicationMapper> execute()
    {
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        return adminApplicationDescriptorService.getAllowedAdminApplicationDescriptors( principals ).
            stream().
            map( adminApplicationDescriptor -> new AdminApplicationMapper( adminApplicationDescriptor ) ).
            collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.adminApplicationDescriptorService = context.getService( AdminApplicationDescriptorService.class ).get();
    }
}
