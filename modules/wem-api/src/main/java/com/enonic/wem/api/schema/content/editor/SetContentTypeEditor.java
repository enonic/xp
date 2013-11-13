package com.enonic.wem.api.schema.content.editor;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;

public final class SetContentTypeEditor
    implements ContentTypeEditor
{
    private final String displayName;

    private final ContentTypeName superType;

    private final Boolean isAbstract;

    private final Boolean isFinal;

    private final Form form;

    private final Icon icon;

    private final String contentDisplayNameScript;

    private SetContentTypeEditor( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.superType = builder.superType;
        this.isAbstract = builder.isAbstract;
        this.isFinal = builder.isFinal;
        this.form = builder.form;
        this.icon = builder.icon;
        this.contentDisplayNameScript = builder.contentDisplayNameScript;
    }

    public static Builder newSetContentTypeEditor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String displayName;

        private ContentTypeName superType;

        private Boolean isAbstract;

        private Boolean isFinal;

        private Form form;

        private Icon icon;

        private String contentDisplayNameScript;

        private Builder()
        {
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder superType( final ContentTypeName superType )
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

        public Builder contentDisplayNameScript( final String contentDisplayNameScript )
        {
            this.contentDisplayNameScript = contentDisplayNameScript;
            return this;
        }

        public SetContentTypeEditor build()
        {
            Preconditions.checkArgument(
                this.displayName != null || this.superType != null || this.isFinal != null || this.isAbstract != null ||
                    this.icon != null || this.form != null || this.contentDisplayNameScript != null, "Missing edit fields" );
            return new SetContentTypeEditor( this );
        }

    }

    @Override
    public ContentType edit( final ContentType contentType )
    {
        final boolean modified = ( this.icon != null ) && !icon.equals( contentType.getIcon() ) ||
            ( this.displayName != null && !displayName.equals( contentType.getDisplayName() ) ) ||
            ( this.contentDisplayNameScript != null && !contentDisplayNameScript.equals( contentType.getContentDisplayNameScript() ) ) ||
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
        if ( this.contentDisplayNameScript != null )
        {
            builder.contentDisplayNameScript( this.contentDisplayNameScript );
        }

        if ( this.icon != null )
        {
            builder.icon( this.icon );
        }

        return builder.build();
    }
}
