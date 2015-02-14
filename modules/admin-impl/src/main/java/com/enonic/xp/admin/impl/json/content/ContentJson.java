package com.enonic.xp.admin.impl.json.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentListJson;
import com.enonic.xp.admin.impl.json.content.page.PageJson;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.admin.impl.rest.resource.content.json.AccessControlEntryJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.Metadata;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.form.InlineMixinsToFormItemsTransformer;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

@SuppressWarnings("UnusedDeclaration")
public final class ContentJson
    extends ContentSummaryJson
{
    private final List<PropertyArrayJson> data;

    private final List<AttachmentJson> attachments;

    private final List<MetadataJson> metadata;

    private final PageJson pageJson;

    private final List<AccessControlEntryJson> accessControlList;

    private final boolean inheritPermissions;

    public ContentJson( final Content content, final ContentIconUrlResolver iconUrlResolver,
                        final InlineMixinsToFormItemsTransformer inlineMixinsToFormItemsTransformer,
                        final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( content, iconUrlResolver );
        this.data = PropertyTreeJson.toJson( content.getData() );
        this.attachments = AttachmentListJson.toJson( content.getAttachments() );

        this.metadata = new ArrayList<>();
        for ( Metadata item : content.getAllMetadata() )
        {
            this.metadata.add( new MetadataJson( item ) );
        }

        this.pageJson = content.hasPage() ? new PageJson( content.getPage() ) : null;

        final Principals principals = contentPrincipalsResolver.resolveAccessControlListPrincipals( content.getPermissions() );
        this.accessControlList = aclToJson( content.getPermissions(), principals );
        this.inheritPermissions = content.inheritsPermissions();
    }

    private List<AccessControlEntryJson> aclToJson( final AccessControlList acl, final Principals principals )
    {
        final List<AccessControlEntryJson> jsonList = new ArrayList<>();
        for ( AccessControlEntry entry : acl )
        {
            jsonList.add( new AccessControlEntryJson( entry, principals.getPrincipal( entry.getPrincipal() ) ) );
        }
        return jsonList;
    }

    public List<PropertyArrayJson> getData()
    {
        return data;
    }

    public List<AttachmentJson> getAttachments()
    {
        return attachments;
    }

    public List<MetadataJson> getMeta()
    {
        return this.metadata;
    }

    public List<AccessControlEntryJson> getPermissions()
    {
        return this.accessControlList;
    }

    public PageJson getPage()
    {
        return pageJson;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }
}
