package com.enonic.xp.project;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.AttachmentNames;
import com.enonic.xp.attachment.CreateAttachment;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class ModifyProjectParams
{
    private final ProjectName name;

    private final ProjectEditor editor;

    private final @Nullable CreateAttachment icon;

    private final boolean removeIcon;

    private ModifyProjectParams( final Builder builder )
    {
        this.name = requireNonNull( builder.name, "name is required" );
        this.editor = requireNonNull( builder.editor, "editor is required" );
        this.icon = builder.icon;
        this.removeIcon = builder.removeIcon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectName getName()
    {
        return name;
    }

    public ProjectEditor getEditor()
    {
        return editor;
    }

    public @Nullable CreateAttachment getIcon()
    {
        return icon;
    }

    public boolean isRemoveIcon()
    {
        return removeIcon;
    }

    public static final class Builder
    {
        private @Nullable ProjectName name;

        private @Nullable ProjectEditor editor;

        private @Nullable CreateAttachment icon;

        private boolean removeIcon;

        private Builder()
        {
        }

        public Builder name( final ProjectName name )
        {
            this.name = name;
            return this;
        }

        public Builder editor( final ProjectEditor editor )
        {
            this.editor = editor;
            return this;
        }

        /**
         * Sets a replacement icon for the project. The icon's name must be {@link AttachmentNames#THUMBNAIL}
         * and the mimeType must start with {@code image/}. Clears any prior {@link #removeIcon()} call.
         */
        public Builder icon( final CreateAttachment icon )
        {
            requireNonNull( icon, "icon is required" );
            Preconditions.checkArgument( AttachmentNames.THUMBNAIL.equals( icon.getName() ), "icon name must be '%s'",
                                         AttachmentNames.THUMBNAIL );
            final String mimeType = requireNonNull( icon.getMimeType(), "icon mimeType is required" );
            Preconditions.checkArgument( MediaType.parse( mimeType ).is( MediaType.ANY_IMAGE_TYPE ),
                                         "icon mimeType must be an image type, got '%s'", mimeType );
            this.icon = icon;
            this.removeIcon = false;
            return this;
        }

        /**
         * Marks the existing project icon for removal. Mutually exclusive with {@link #icon(CreateAttachment)};
         * the last-called setter wins. Without either call the icon is left unchanged.
         */
        public Builder removeIcon()
        {
            this.icon = null;
            this.removeIcon = true;
            return this;
        }

        public ModifyProjectParams build()
        {
            return new ModifyProjectParams( this );
        }
    }
}