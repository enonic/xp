package com.enonic.wem.api.content.schema.content.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.form.Form;

import static com.enonic.wem.api.content.schema.content.ContentType.newContentType;

public final class SetContentTypeEditor
    implements ContentTypeEditor
{
    private final String displayName;

    private final QualifiedContentTypeName superType;

    private final Boolean isAbstract;

    private final Boolean isFinal;

    private final Form form;

    private final Icon icon;

    private SetContentTypeEditor( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.superType = builder.superType;
        this.isAbstract = builder.isAbstract;
        this.isFinal = builder.isFinal;
        this.form = builder.form;
        this.icon = builder.icon;
    }

    public static Builder newSetContentTypeEditor()
    {
        return new Builder();
    }

    public static Builder newSetContentTypeEditor( final ContentType contentType )
    {
        return new Builder( contentType );
    }

    public static class Builder
    {
        private String displayName;

        private QualifiedContentTypeName superType;

        private Boolean isAbstract;

        private Boolean isFinal;

        private Form form;

        private Icon icon;

        private Builder()
        {
        }

        private Builder( final ContentType contentType )
        {
            displayName = contentType.getDisplayName();
            superType = contentType.getSuperType();
            isAbstract = contentType.isAbstract();
            isFinal = contentType.isFinal();
            form = contentType.form();
            icon = contentType.getIcon();
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder superType( final QualifiedContentTypeName superType )
        {
            this.superType = superType;
            return this;
        }

        public Builder setAbstract( final Boolean isAbstract )
        {
            this.isAbstract = isAbstract;
            return this;
        }

        public Builder setFinal( final Boolean isFinal )
        {
            this.isFinal = isFinal;
            return this;
        }

        public Builder form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder icon( final Icon icon )
        {
            this.icon = icon;
            return this;
        }

        public SetContentTypeEditor build()
        {
            return new SetContentTypeEditor( this );
        }

    }

    @Override
    public ContentType edit( final ContentType contentType )
        throws Exception
    {
        final boolean modified = ( this.icon != null ) ||
            ( this.displayName != null && !displayName.equals( contentType.getDisplayName() ) ) ||
            ( this.superType != null && !superType.equals( contentType.getSuperType() ) ) ||
            ( this.isAbstract != contentType.isAbstract() ) ||
            ( this.isFinal != contentType.isFinal() ) ||
            ( this.form != null );
        if ( !modified )
        {
            return null;
        }

        final ContentType.Builder builder = newContentType( contentType );
        if ( this.displayName != null )
        {
            builder.displayName( this.displayName );
        }
        if ( this.superType != null )
        {
            builder.superType( this.superType );
        }
        if ( this.isAbstract != null )
        {
            builder.setAbstract( this.isAbstract );
        }
        if ( this.isFinal != null )
        {
            builder.setFinal( this.isFinal );
        }
        if ( this.form != null )
        {
            builder.form( this.form );
        }

        final Icon iconToSet = ( this.icon == null ) ? null : Icon.copyOf( icon );
        if ( iconToSet != null )
        {
            builder.icon( iconToSet );
        }

        return builder.build();
    }
}
