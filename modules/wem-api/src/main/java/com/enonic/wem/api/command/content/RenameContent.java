package com.enonic.wem.api.command.content;


import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;

public final class RenameContent
    extends Command<Boolean>
{
    private ContentId contentId;

    private String newName;

    public RenameContent()
    {
    }

    public RenameContent contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public RenameContent newName( final String newName )
    {
        this.newName = newName;
        return this;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public String getNewName()
    {
        return newName;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof RenameContent ) )
        {
            return false;
        }

        final RenameContent that = (RenameContent) o;
        return Objects.equal( this.contentId, that.contentId ) && Objects.equal( this.newName, that.newName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentId, this.newName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
        Preconditions.checkNotNull( this.newName, "name cannot be null" );
    }
}
