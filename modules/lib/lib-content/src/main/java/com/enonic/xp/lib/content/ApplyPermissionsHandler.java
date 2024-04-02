package com.enonic.xp.lib.content;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class ApplyPermissionsHandler
    implements ScriptBean
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplyPermissionsHandler.class );

    private ContentService contentService;

    private SecurityService securityService;

    private String key;

    private boolean overwriteChildPermissions;

    private AccessControlList permissions;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setOverwriteChildPermissions( final boolean overwriteChildPermissions )
    {
        this.overwriteChildPermissions = overwriteChildPermissions;
    }

    public void setPermissions( final ScriptValue permissions )
    {
        if ( permissions != null )
        {
            final List<AccessControlEntry> accessControlEntries =
                permissions.getArray().stream().map( this::convertToAccessControlEntry ).collect( Collectors.toList() );

            this.permissions = AccessControlList.create().addAll( accessControlEntries ).build();
        }
    }

    private AccessControlEntry convertToAccessControlEntry( ScriptValue permission )
    {
        final String principal = permission.getMember( "principal" ).getValue( String.class );
        final List<Permission> allowedPermissions =
            permission.getMember( "allow" ).getArray( String.class ).stream().map( Permission::valueOf ).collect( Collectors.toList() );
        final List<Permission> deniedPermissions =
            permission.getMember( "deny" ).getArray( String.class ).stream().map( Permission::valueOf ).collect( Collectors.toList() );

        return AccessControlEntry.create()
            .principal( PrincipalKey.from( principal ) )
            .allow( allowedPermissions )
            .deny( deniedPermissions )
            .build();
    }

    public boolean execute()
    {
        return doExecute();
    }

    private boolean doExecute()
    {
        ContentId contentId = getContentId();

        if ( !validPrincipals() )
        {
            return false;
        }

        if ( contentId != null )
        {
            contentService.applyPermissions( ApplyContentPermissionsParams.create()
                                                 .contentId( contentId )
                                                 .permissions( permissions )
                                                 .overwriteChildPermissions( overwriteChildPermissions )
                                                 .build() );

            return true;
        }

        return false;
    }

    private ContentId getContentId()
    {
        try
        {
            if ( this.key.startsWith( "/" ) )
            {
                final Content content = this.contentService.getByPath( ContentPath.from( this.key ) );
                if ( content != null )
                {
                    return content.getId();
                }
            }
            else
            {
                return ContentId.from( this.key );
            }
        }
        catch ( final ContentNotFoundException e )
        {
            LOG.warn( "Content not found: " + this.key );
        }

        return null;
    }

    private boolean validPrincipals()
    {
        boolean valid = true;
        for ( PrincipalKey principal : permissions.getAllPrincipals() )
        {
            if ( !principalExists( principal ) )
            {
                LOG.warn( "Principal not found: " + principal );
                valid = false;
            }
        }
        return valid;
    }

    private boolean principalExists( final PrincipalKey principal )
    {
        return securityService.getPrincipal( principal ).isPresent();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
        this.securityService = context.getService( SecurityService.class ).get();
    }
}
