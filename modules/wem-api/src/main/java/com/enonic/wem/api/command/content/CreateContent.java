package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;

public final class CreateContent
    extends Command<CreateContentResult>
{
    private RootDataSet rootDataSet;

    private QualifiedContentTypeName contentType;

    private UserKey owner;

    private String displayName;

    private String name;

    private ContentPath parentContentPath;

    private boolean temporary;

    public CreateContent contentType( final QualifiedContentTypeName value )
    {
        this.contentType = value;
        return this;
    }

    public CreateContent parentContentPath( final ContentPath parentContentPath )
    {
        this.parentContentPath = parentContentPath;
        return this;
    }

    public CreateContent rootDataSet( final RootDataSet value )
    {
        this.rootDataSet = value;
        return this;
    }

    public CreateContent owner( final UserKey owner )
    {
        this.owner = owner;
        return this;
    }

    public CreateContent displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateContent name( final String name )
    {
        this.name = name;
        return this;
    }

    public CreateContent temporary()
    {
        this.temporary = true;
        return this;
    }

    public CreateContent temporary( final boolean createTemporaryContent )
    {
        this.temporary = createTemporaryContent;
        return this;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public QualifiedContentTypeName getContentType()
    {
        return contentType;
    }

    public RootDataSet getRootDataSet()
    {
        return rootDataSet;
    }

    public UserKey getOwner()
    {
        return owner;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getName()
    {
        return name;
    }

    public boolean isTemporary()
    {
        return temporary;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.rootDataSet, "rootDataSet cannot be null" );
        Preconditions.checkArgument( temporary || this.parentContentPath != null, "parentContentPath cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayname cannot be null" );
    }
}
