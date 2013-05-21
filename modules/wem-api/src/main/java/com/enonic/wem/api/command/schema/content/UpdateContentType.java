package com.enonic.wem.api.command.schema.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.editor.ContentTypeEditor;

public final class UpdateContentType
    extends Command<UpdateContentTypeResult>
{
    private QualifiedContentTypeName qualifiedName;

    private ContentTypeEditor editor;


    public UpdateContentType qualifiedName( final QualifiedContentTypeName qualifiedName )
    {
        this.qualifiedName = qualifiedName;
        return this;
    }

    public UpdateContentType editor( final ContentTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public QualifiedContentTypeName getQualifiedName()
    {
        return qualifiedName;
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
        return Objects.equal( this.qualifiedName, that.qualifiedName ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedName, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedName, "qualifiedName cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
