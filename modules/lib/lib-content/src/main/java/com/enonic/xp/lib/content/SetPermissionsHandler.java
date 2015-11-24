package com.enonic.xp.lib.content;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SetPermissionsHandler
    extends BaseContextHandler
{
    private String key;

    private boolean inheritPermissions;

    private boolean overwriteChildPermissions;

    private AccessControlList permissions;


    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setInheritPermissions( final boolean inheritPermissions )
    {
        this.inheritPermissions = inheritPermissions;
    }

    public void setOverwriteChildPermissions( final boolean overwriteChildPermissions )
    {
        this.overwriteChildPermissions = overwriteChildPermissions;
    }

    public void setPermissions( final ScriptValue permissions )
    {
        if ( permissions != null )
        {
            final List<AccessControlEntry> accessControlEntries = permissions.getArray().
                stream().
                map( permission -> {
                    final String principal = permission.getMember( "principal" ).
                        getValue( String.class );
                    final List<Permission> allowedPermissions = permission.getMember( "allow" ).
                        getArray( String.class ).
                        stream().
                        map( Permission::valueOf ).
                        collect( Collectors.toList() );
                    final List<Permission> deniedPermissions = permission.getMember( "deny" ).
                        getArray( String.class ).
                        stream().
                        map( Permission::valueOf ).
                        collect( Collectors.toList() );

                    return AccessControlEntry.create().
                        principal( PrincipalKey.from( principal ) ).
                        allow( allowedPermissions ).
                        deny( deniedPermissions ).
                        build();
                } ).
                collect( Collectors.toList() );

            this.permissions = AccessControlList.
                create().
                addAll( accessControlEntries ).
                build();
        }
    }

    @Override
    protected Object doExecute()
    {
        ContentId contentId = getContentId();

        if ( contentId != null )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            final PrincipalKey modifier =
                authInfo != null && authInfo.isAuthenticated() ? authInfo.getUser().getKey() : PrincipalKey.ofAnonymous();

            final UpdateContentParams updatePermissionsParams = new UpdateContentParams().
                contentId( contentId ).
                modifier( modifier ).
                editor( edit -> {
                    edit.inheritPermissions = inheritPermissions;
                    edit.permissions = permissions;
                } );
            final Content updatedContent = contentService.update( updatePermissionsParams );

            contentService.applyPermissions( ApplyContentPermissionsParams.create().
                contentId( updatedContent.getId() ).
                overwriteChildPermissions( overwriteChildPermissions ).
                build() );

            return new ContentMapper( updatedContent );
        }

        return null;
    }

    private ContentId getContentId()
    {
        try
        {
            if ( this.key.startsWith( "/" ) )
            {
                final Content content = this.contentService.getByPath( ContentPath.from( key ) );
                if ( content != null )
                {
                    return content.getId();
                }
            }
            else
            {
                return ContentId.from( key );
            }
        }
        catch ( final ContentNotFoundException e )
        {
        }
        return null;
    }
}
