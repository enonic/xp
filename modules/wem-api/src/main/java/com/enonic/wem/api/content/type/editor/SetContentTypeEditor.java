package com.enonic.wem.api.content.type.editor;

import java.util.Arrays;

import com.enonic.wem.api.content.type.ContentType;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

final class SetContentTypeEditor
    implements ContentTypeEditor
{
    private final ContentType source;

    SetContentTypeEditor( final ContentType source )
    {
        this.source = source;
    }

    @Override
    public ContentType edit( final ContentType contentType )
        throws Exception
    {
        final byte[] iconToSet = ( source.getIcon() == null ) ? null : Arrays.copyOf( source.getIcon(), source.getIcon().length );
        final ContentType.Builder builder = newContentType( contentType );
        builder.displayName( source.getDisplayName() );
        builder.superType( source.getSuperType() );
        builder.setAbstract( source.isAbstract() );
        builder.setFinal( source.isFinal() );
        builder.icon( iconToSet );
        if ( source.form() == null )
        {
            // nothing
        }
        else
        {
            builder.form( source.form() );
        }

        return builder.build();
    }
}
