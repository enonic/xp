package com.enonic.wem.admin.json.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.enonic.wem.admin.json.content.page.PageJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.admin.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.wem.admin.rest.resource.content.json.AccessControlEntryJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.DataSetJson;
import com.enonic.wem.api.form.FormJson;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

@SuppressWarnings("UnusedDeclaration")
public final class ContentJson
    extends ContentSummaryJson
{
    private final DataSetJson data;

    private final List<MetadataJson> metadata;

    private final FormJson form;

    private final PageJson pageJson;

    private final List<AccessControlEntryJson> accessControlList;

    private final List<AccessControlEntryJson> parentAccessControlList;

    public ContentJson( final Content content, final ContentIconUrlResolver iconUrlResolver,
                        final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer,
                        final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        this( content, null, iconUrlResolver, mixinReferencesToFormItemsTransformer, contentPrincipalsResolver );
    }

    public ContentJson( final Content content, final AccessControlList parentAcl, final ContentIconUrlResolver iconUrlResolver,
                        final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer,
                        final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        super( content, iconUrlResolver );
        this.data = new DataSetJson( content.getContentData() );

        this.metadata = new ArrayList<>();
        final List<Metadata> metadataList = content.getAllMetadata();
        if ( metadataList != null )
        {
            for ( Metadata item : metadataList )
            {
                this.metadata.add( new MetadataJson( item ) );
            }
        }

        this.form = FormJson.resolveJson( content.getForm(), mixinReferencesToFormItemsTransformer );
        this.pageJson = content.hasPage() ? new PageJson( content.getPage() ) : null;

        final Principals principals = contentPrincipalsResolver.resolveAccessControlListPrincipals( content );
        this.accessControlList = aclToJson( content.getAccessControlList(), principals );
        if ( parentAcl != null )
        {
            this.parentAccessControlList = aclToJson( parentAcl, principals );
        }
        else
        {
            this.parentAccessControlList = Collections.EMPTY_LIST;
        }
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

    public List<DataJson> getData()
    {
        return data.getValue();
    }

    public List<MetadataJson> getMetadata()
    {
        return this.metadata;
    }

    public List<AccessControlEntryJson> getPermissions()
    {
        return this.accessControlList;
    }

    public List<AccessControlEntryJson> getInheritedPermissions()
    {
        return this.parentAccessControlList;
    }

    public FormJson getForm()
    {
        return form;
    }

    public PageJson getPage()
    {
        return pageJson;
    }
}
