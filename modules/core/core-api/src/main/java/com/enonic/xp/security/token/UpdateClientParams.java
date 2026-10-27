package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;

import static java.util.Objects.requireNonNull;

/**
 * A change to a client: which one, and an editor that makes the change on an editable copy.
 */
@NullMarked
public final class UpdateClientParams
{
    private final IdProviderKey idProvider;

    private final String id;

    private final ClientEditor editor;

    private UpdateClientParams( final Builder builder )
    {
        this.idProvider = requireNonNull( builder.idProvider, "idProvider is required" );
        this.id = requireNonNull( builder.id, "id is required" );
        this.editor = requireNonNull( builder.editor, "editor is required" );
    }

    /**
     * A builder for the parameters.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The id provider the client belongs to.
     */
    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    /**
     * The client to change.
     */
    public String getId()
    {
        return id;
    }

    /**
     * The change to apply.
     */
    public ClientEditor getEditor()
    {
        return editor;
    }

    /**
     * The client with the change applied.
     *
     * @throws IllegalArgumentException when the editor set an invalid value, see {@link Client.Builder#build()}
     */
    public Client applyTo( final Client client )
    {
        final EditableClient editable = new EditableClient( client );
        editor.edit( editable );
        return editable.build();
    }

    /**
     * Collects the id provider, the id and the editor; all three are required.
     */
    public static final class Builder
    {
        private @Nullable IdProviderKey idProvider;

        private @Nullable String id;

        private @Nullable ClientEditor editor;

        private Builder()
        {
        }

        /**
         * The id provider the client belongs to. Required.
         */
        public Builder idProvider( final IdProviderKey idProvider )
        {
            this.idProvider = idProvider;
            return this;
        }

        /**
         * The client to change. Required.
         */
        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        /**
         * What to change. Required.
         */
        public Builder editor( final ClientEditor editor )
        {
            this.editor = editor;
            return this;
        }

        /**
         * @throws NullPointerException when the id provider, the id or the editor is missing
         */
        public UpdateClientParams build()
        {
            return new UpdateClientParams( this );
        }
    }
}
