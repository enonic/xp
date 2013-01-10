package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.ContentType;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

final class SetContentTypeEditor
    implements ContentTypeEditor
{
    protected final ContentType source;

    public SetContentTypeEditor( final ContentType source )
    {
        this.source = source;
    }

    @Override
    public ContentType edit( final ContentType contentType )
        throws Exception
    {
        final ContentType updated = newContentType( contentType ).
            displayName( source.getDisplayName() ).
            superType( source.getSuperType() ).
            setAbstract( source.isAbstract() ).
            setFinal( source.isFinal() ).
            formItems( source.form().getFormItems().copy() ).
            // TODO: add more fields to update?
                build();
        return updated;
    }
}
