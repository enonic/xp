package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.security.acl.AccessControlList;

public class ApplyContentPermissionsJson
{
    final UpdateContentParams updateContentParams;

    final boolean overwriteChildPermissions;

    @JsonCreator
    ApplyContentPermissionsJson( @JsonProperty("contentId") final String contentId,
                                 @JsonProperty("permissions") final List<AccessControlEntryJson> permissions,
                                 @JsonProperty("inheritPermissions") final boolean inheritPermissions,
                                 @JsonProperty("overwriteChildPermissions") final boolean overwriteChildPermissions )
    {
        this.updateContentParams = new UpdateContentParams().
            contentId( ContentId.from( contentId ) ).
            editor( edit -> {
                edit.inheritPermissions = inheritPermissions;
                edit.permissions = parseAcl( permissions );
            } );

        this.overwriteChildPermissions = overwriteChildPermissions;
    }

    @JsonIgnore
    public UpdateContentParams getUpdateContentParams()
    {
        return updateContentParams;
    }

    @JsonIgnore
    public boolean isOverwriteChildPermissions()
    {
        return overwriteChildPermissions;
    }

    private AccessControlList parseAcl( final List<AccessControlEntryJson> accessControlListJson )
    {
        final AccessControlList.Builder builder = AccessControlList.create();
        for ( final AccessControlEntryJson entryJson : accessControlListJson )
        {
            builder.add( entryJson.getSourceEntry() );
        }
        return builder.build();
    }
}
