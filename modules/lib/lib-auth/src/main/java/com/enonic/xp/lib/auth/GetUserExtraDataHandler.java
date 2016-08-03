package com.enonic.xp.lib.auth;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.lib.content.mapper.PropertyTreeMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

public final class GetUserExtraDataHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private PrincipalKey key;

    private String namespace;

    public void setKey( final String key )
    {
        this.key = PrincipalKey.from( key );
    }

    public void setNamespace( final String namespace )
    {
        this.namespace = namespace;
    }

    public PropertyTreeMapper execute()
    {
        final Optional<User> user = this.securityService.get().
            getUser( this.key );

        if ( user.isPresent() )
        {
            final ImmutableMap<String, PropertySet> extraDataMap = user.get().getExtraDataMap();
            final PropertySet extraData = extraDataMap.get( this.namespace );
            if ( extraData != null )
            {
                return new PropertyTreeMapper( extraData.toTree() );
            }
        }

        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
