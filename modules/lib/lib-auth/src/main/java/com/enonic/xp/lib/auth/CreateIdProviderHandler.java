package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.lib.common.IdProviderMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.acl.IdProviderAccess;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;
import com.enonic.xp.security.acl.IdProviderAccessControlList;

public final class CreateIdProviderHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private String key;

    private String displayName;

    private String description;

    private ScriptValue permissions;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public void setPermissions( final ScriptValue permissions )
    {
        this.permissions = permissions;
    }

    public IdProviderMapper createIdProvider()
    {
        final CreateIdProviderParams params = CreateIdProviderParams.create()
            .key( IdProviderKey.from( this.key ) )
            .displayName( this.displayName )
            .description( this.description )
            .permissions( buildPermissions() )
            .build();

        final IdProvider idProvider = this.securityService.get().createIdProvider( params );
        return new IdProviderMapper( idProvider );
    }

    private IdProviderAccessControlList buildPermissions()
    {
        if ( this.permissions == null )
        {
            return null;
        }
        final List<ScriptValue> entries = this.permissions.getArray();
        if ( entries.isEmpty() )
        {
            return null;
        }
        final IdProviderAccessControlList.Builder builder = IdProviderAccessControlList.create();
        for ( final ScriptValue entry : entries )
        {
            final Map<String, Object> entryMap = entry.getMap();
            final Object principal = entryMap.get( "principal" );
            final Object access = entryMap.get( "access" );
            if ( principal == null || access == null )
            {
                throw new IllegalArgumentException( "Permission entries require 'principal' and 'access' fields" );
            }
            builder.add( IdProviderAccessControlEntry.create()
                             .principal( PrincipalKey.from( principal.toString() ) )
                             .access( IdProviderAccess.valueOf( access.toString().toUpperCase( Locale.ROOT ) ) )
                             .build() );
        }
        return builder.build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
