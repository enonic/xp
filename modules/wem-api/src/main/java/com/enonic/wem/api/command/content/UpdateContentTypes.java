package com.enonic.wem.api.command.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.editor.ContentTypeEditor;

public final class UpdateContentTypes
    extends Command<Integer>
{
    private QualifiedContentTypeNames contentTypeNames;

    private ContentTypeEditor editor;


    public UpdateContentTypes names( final QualifiedContentTypeNames contentTypeNames )
    {
        this.contentTypeNames = contentTypeNames;
        return this;
    }

    public UpdateContentTypes editor( final ContentTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public QualifiedContentTypeNames getNames()
    {
        return contentTypeNames;
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

        if ( !( o instanceof UpdateContentTypes ) )
        {
            return false;
        }

        final UpdateContentTypes that = (UpdateContentTypes) o;
        return Objects.equal( this.contentTypeNames, that.contentTypeNames ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentTypeNames, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeNames, "Content type names cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
