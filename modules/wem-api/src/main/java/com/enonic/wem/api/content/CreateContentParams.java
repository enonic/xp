package com.enonic.wem.api.content;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;

public final class CreateContentParams
{
    private Form form;

    private PropertyTree data;

    private List<Metadata> metadata;

    private ContentTypeName contentType;

    private PrincipalKey owner;

    private String displayName;

    private ContentName name;

    private ContentPath parentContentPath;

    private boolean draft;

    private Map<String, Attachment> attachments = Maps.newHashMap();

    private Set<OrderExpr> orderExpressions = Sets.newLinkedHashSet();

    private AccessControlList accessControlList;

    private boolean inheritPermissions;

    public CreateContentParams contentType( final ContentTypeName value )
    {
        this.contentType = value;
        return this;
    }

    public CreateContentParams parent( final ContentPath parentContentPath )
    {
        this.parentContentPath = parentContentPath;
        return this;
    }

    public CreateContentParams form( final Form value )
    {
        this.form = value;
        return this;
    }

    public CreateContentParams contentData( final PropertyTree value )
    {
        this.data = value;
        return this;
    }

    public CreateContentParams metadata( final List<Metadata> metadata )
    {
        this.metadata = metadata;
        return this;
    }

    public CreateContentParams owner( final PrincipalKey owner )
    {
        this.owner = owner;
        return this;
    }

    public CreateContentParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateContentParams name( final String name )
    {
        this.name = ContentName.from( name );
        return this;
    }

    public CreateContentParams name( final ContentName name )
    {
        this.name = name;
        return this;
    }

    public CreateContentParams draft()
    {
        this.draft = true;
        return this;
    }

    public CreateContentParams draft( final boolean value )
    {
        this.draft = value;
        return this;
    }

    public CreateContentParams addOrderExpression( final OrderExpr orderExpression )
    {
        this.orderExpressions.add( orderExpression );
        return this;
    }

    public CreateContentParams attachments( final Attachment... attachments )
    {
        for ( Attachment attachment : attachments )
        {
            if ( attachment == null )
            {
                continue;
            }

            Preconditions.checkArgument( !this.attachments.containsKey( attachment.getName() ), "attachment with duplicated name: %s",
                                         attachment.getName() );
            this.attachments.put( attachment.getName(), attachment );
        }
        return this;
    }

    public CreateContentParams attachments( final Iterable<Attachment> attachments )
    {
        return attachments( Iterables.toArray( attachments, Attachment.class ) );
    }

    public CreateContentParams accessControlList( final AccessControlList accessControlList )
    {
        this.accessControlList = accessControlList;
        return this;
    }

    public CreateContentParams setInheritPermissions( final boolean inheritPermissions )
    {
        this.inheritPermissions = inheritPermissions;
        return this;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public ContentTypeName getContentType()
    {
        return contentType;
    }

    public Form getForm()
    {
        return form;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public List<Metadata> getMetadata()
    {
        return metadata;
    }

    public PrincipalKey getOwner()
    {
        return owner;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public ContentName getName()
    {
        return name;
    }

    public boolean isDraft()
    {
        return draft;
    }

    public Attachments getAttachments()
    {
        return Attachments.from( attachments.values() );
    }

    public Attachment getAttachment( final String attachmentName )
    {
        return attachments.get( attachmentName );
    }

    public Set<OrderExpr> getOrderExpressions()
    {
        return orderExpressions;
    }

    public AccessControlList getAccessControlList()
    {
        return accessControlList;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.data, "data cannot be null" );
        Preconditions.checkArgument( draft || this.parentContentPath != null, "parentContentPath cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
