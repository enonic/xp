package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;

import static java.util.Objects.requireNonNull;

/**
 * A change to a scope: which one, and an editor that makes the change on an editable copy.
 */
@NullMarked
public final class UpdateScopeParams
{
    private final IdProviderKey idProvider;

    private final String id;

    private final ScopeEditor editor;

    private UpdateScopeParams( final Builder builder )
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
     * The id provider the scope belongs to.
     */
    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    /**
     * The scope to change.
     */
    public String getId()
    {
        return id;
    }

    /**
     * The change to apply.
     */
    public ScopeEditor getEditor()
    {
        return editor;
    }

    /**
     * The scope with the change applied.
     *
     * @throws IllegalArgumentException when the editor set a principal that is not a role
     */
    public Scope applyTo( final Scope scope )
    {
        final EditableScope editable = new EditableScope( scope );
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

        private @Nullable ScopeEditor editor;

        private Builder()
        {
        }

        /**
         * The id provider the scope belongs to. Required.
         */
        public Builder idProvider( final IdProviderKey idProvider )
        {
            this.idProvider = idProvider;
            return this;
        }

        /**
         * The scope to change. Required.
         */
        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        /**
         * What to change. Required.
         */
        public Builder editor( final ScopeEditor editor )
        {
            this.editor = editor;
            return this;
        }

        /**
         * @throws NullPointerException when the id provider, the id or the editor is missing
         */
        public UpdateScopeParams build()
        {
            return new UpdateScopeParams( this );
        }
    }
}
