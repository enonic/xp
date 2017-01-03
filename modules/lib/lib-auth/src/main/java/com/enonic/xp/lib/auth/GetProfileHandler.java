package com.enonic.xp.lib.auth;

import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

public final class GetProfileHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private PrincipalKey key;

    private String scope;

    public void setKey( final String key )
    {
        this.key = PrincipalKey.from( key );
    }

    public void setScope( final String scope )
    {
        this.scope = scope;
    }

    public PropertyTreeMapper execute()
    {
        final Optional<User> user = this.securityService.get().
            getUser( this.key );

        if ( user.isPresent() )
        {
            final PropertyTree profile = user.get().getProfile();
            if ( profile != null )
            {
                if ( scope == null )
                {
                    return new PropertyTreeMapper( profile );
                }
                else
                {
                    final PropertySet scopedProfile = profile.getSet( scope );
                    return scopedProfile == null ? null : new PropertyTreeMapper( scopedProfile.toTree() );
                }
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
