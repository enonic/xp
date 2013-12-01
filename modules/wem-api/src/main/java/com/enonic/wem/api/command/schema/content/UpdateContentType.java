package com.enonic.wem.api.command.schema.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;

public final class UpdateContentType
    extends Command<UpdateContentTypeResult>
{
    private ContentTypeName name;

    private ContentTypeEditor editor;

    public UpdateContentType contentTypeName( final ContentTypeName name )
    {
        this.name = name;
        return this;
    }

    public UpdateContentType editor( final ContentTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public ContentTypeName getContentTypeName()
    {
        return name;
    }

    public ContentTypeEditor getEditor()
    {
        return editor;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof UpdateContentType ) )
        {
            return false;
        }

        final UpdateContentType that = (UpdateContentType) o;
        return Objects.equal( this.name, that.name ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.name, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
