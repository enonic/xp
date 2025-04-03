package com.enonic.xp.lib.repo;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.repo.mapper.RepositoryMapper;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

@SuppressWarnings("unused")
public class CreateRepositoryHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private AccessControlList rootPermissions;

    private ChildOrder rootChildOrder;

    private boolean transientFlag;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId == null ? null : RepositoryId.from( repositoryId );
    }

    public void setRootPermissions( final ScriptValue rootPermissions )
    {
        if ( rootPermissions != null )
        {
            final List<AccessControlEntry> accessControlEntries = rootPermissions.getArray().
                stream().
                map( this::convertToAccessControlEntry ).
                collect( Collectors.toList() );

            this.rootPermissions = AccessControlList.
                create().
                addAll( accessControlEntries ).
                build();
        }
    }

    public void setRootChildOrder( final String rootChildOrder )
    {
        if ( rootChildOrder != null )
        {
            this.rootChildOrder = ChildOrder.from( rootChildOrder );
        }
    }


    public void setTransient( final boolean value )
    {
        this.transientFlag = value;
    }

    public RepositoryMapper execute()
    {
        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
            repositoryId( repositoryId ).
            rootPermissions( rootPermissions ).
            rootChildOrder( rootChildOrder ).
            transientFlag( transientFlag ).
            build();

        final Repository repository = repositoryServiceSupplier.
            get().
            createRepository( createRepositoryParams );

        return new RepositoryMapper( repository );
    }

    private AccessControlEntry convertToAccessControlEntry( ScriptValue permission )
    {
        final String principal = permission.getMember( "principal" ).
            getValue( String.class );
        final List<Permission> allowedPermissions = resolvePermissions( permission.getMember( "allow" ) );
        final List<Permission> deniedPermissions = resolvePermissions( permission.getMember( "deny" ) );

        return AccessControlEntry.create().
            principal( PrincipalKey.from( principal ) ).
            allow( allowedPermissions ).
            deny( deniedPermissions ).
            build();
    }

    private List<Permission> resolvePermissions( ScriptValue permissions )
    {
        if ( permissions == null )
        {
            return List.of();
        }

        return permissions.getArray( String.class ).
            stream().
            map( Permission::valueOf ).
            collect( Collectors.toUnmodifiableList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
