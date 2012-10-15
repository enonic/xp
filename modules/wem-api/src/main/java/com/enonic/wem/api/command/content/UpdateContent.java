package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public final class UpdateContent
    extends Command<ContentPath>
{
    private ContentPath contentPath;

    private ContentData contentData;

    private QualifiedContentTypeName contentType;

    private ContentEditor contentEditor;

    public UpdateContent contentPath( ContentPath value )
    {
        this.contentPath = value;
        return this;
    }

    public UpdateContent contentType( QualifiedContentTypeName value )
    {
        this.contentType = value;
        return this;
    }

    public UpdateContent contentData( ContentData contentData )
    {
        this.contentData = contentData;
        return this;
    }

    public UpdateContent editor( final ContentEditor value )
    {
        contentEditor = value;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentPath, "contentPath cannot be null" );
        Preconditions.checkNotNull( this.contentData, "contentData cannot be null" );
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public ContentData getContentData()
    {
        return contentData;
    }

    public QualifiedContentTypeName getContentType()
    {
        return contentType;
    }

    public ContentEditor getContentEditor()
    {
        return contentEditor;
    }
}
