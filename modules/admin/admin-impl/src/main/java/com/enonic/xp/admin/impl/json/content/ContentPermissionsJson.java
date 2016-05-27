package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.security.acl.AccessControlList;

public class ContentPermissionsJson
    extends RootPermissionsJson
{
    private final String contentId;

    public ContentPermissionsJson( final String contentId, final AccessControlList contentPermissions,
                                   final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( contentPermissions, contentPrincipalsResolver );
        this.contentId = contentId;
    }

    public String getContentId()
    {
        return contentId;
    }
}
