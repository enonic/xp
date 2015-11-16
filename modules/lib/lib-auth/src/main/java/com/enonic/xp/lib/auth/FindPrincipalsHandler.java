package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;

public final class FindPrincipalsHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private PrincipalType type;

    private UserStoreKey userStore;

    private int start = 0;

    private int count = 10;

    private String name;

    private String email;

    private String displayName;

    private String searchText;

    public void setSearchText( final String value )
    {
        this.searchText = value;
    }

    public void setType( final String type )
    {
        if ( type == null || type.trim().isEmpty() )
        {
            this.type = null;
            return;
        }
        switch ( type.trim().toLowerCase() )
        {
            case "group":
                this.type = PrincipalType.GROUP;
                return;
            case "role":
                this.type = PrincipalType.ROLE;
                return;
            case "user":
                this.type = PrincipalType.USER;
                return;
            default:
                throw new IllegalArgumentException( "Invalid principal type: '" + type + "'" );
        }
    }

    public void setUserStore( final String userStore )
    {
        if ( userStore == null || userStore.trim().isEmpty() )
        {
            this.userStore = null;
            return;
        }
        this.userStore = UserStoreKey.from( userStore );
    }

    public void setStart( final Integer start )
    {
        if ( start != null )
        {
            this.start = start;
        }
    }

    public void setCount( final Integer count )
    {
        if ( count != null )
        {
            this.count = count;
        }
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public PrincipalsResultMapper findPrincipals()
    {
        final PrincipalQuery.Builder query = PrincipalQuery.create();
        if ( this.type != null )
        {
            query.includeTypes( this.type );
        }
        if ( this.userStore != null )
        {
            query.userStore( this.userStore );
        }
        query.email( this.email );
        query.name( this.name );
        query.displayName( this.displayName );
        query.from( this.start );
        query.size( this.count );
        query.searchText( this.searchText );

        final PrincipalQueryResult result = this.securityService.get().query( query.build() );

        return new PrincipalsResultMapper( result.getPrincipals(), result.getTotalSize() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
