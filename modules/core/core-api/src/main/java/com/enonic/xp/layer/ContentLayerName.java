package com.enonic.xp.layer;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;

@Beta
public final class ContentLayerName
{
    public static final ContentLayerName DEFAULT_LAYER_NAME = ContentLayerName.from( "default" );

    private static final String VALID_REPOSITORY_ID_REGEX = "([a-zA-Z0-9\\-:])([a-zA-Z0-9_\\-\\.:])*";

    private final String value;

    private ContentLayerName( final Builder builder )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( builder.value ), "name cannot be null or empty" );
        Preconditions.checkArgument( builder.value.matches( "^" + VALID_REPOSITORY_ID_REGEX + "$" ),
                                     "name format incorrect: " + builder.value );
        this.value = builder.value;
    }

    public static ContentLayerName from( final String name )
    {
        return ContentLayerName.create().
            value( name ).
            build();
    }

    public String getValue()
    {
        return value;
    }


    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ContentLayerName branch = (ContentLayerName) o;
        return value.equals( branch.value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    public static ContentLayerName current()
    {
        final Branch branch = ContextAccessor.current().getBranch();
        return from( branch );
    }

    public static ContentLayerName from( final Branch branch )
    {
        if ( branch != null )
        {
            if ( ContentConstants.BRANCH_DRAFT.equals( branch ) || ContentConstants.BRANCH_MASTER.equals( branch ) )
            {
                return DEFAULT_LAYER_NAME;
            }
            if ( branch.getValue().startsWith( ContentLayerConstants.BRANCH_PREFIX_DRAFT ) )
            {
                return ContentLayerName.from( branch.getValue().substring( ContentLayerConstants.BRANCH_PREFIX_DRAFT.length() ) );
            }
            if ( branch.getValue().startsWith( ContentLayerConstants.BRANCH_PREFIX_MASTER ) )
            {
                return ContentLayerName.from( branch.getValue().substring( ContentLayerConstants.BRANCH_PREFIX_MASTER.length() ) );
            }
        }
        return null;
    }

    public static boolean isDraftBranch( final Branch branch )
    {
        return ContentConstants.BRANCH_DRAFT.equals( branch ) || branch.getValue().startsWith( ContentLayerConstants.BRANCH_PREFIX_DRAFT );
    }

    public static boolean isMasterBranch( final Branch branch )
    {
        return ContentConstants.BRANCH_MASTER.equals( branch ) ||
            branch.getValue().startsWith( ContentLayerConstants.BRANCH_PREFIX_MASTER );
    }

    public boolean isDefault()
    {
        return DEFAULT_LAYER_NAME.equals( this );
    }

    public Branch getDraftBranch()
    {
        return isDefault() ? ContentConstants.BRANCH_DRAFT : Branch.from( ContentLayerConstants.BRANCH_PREFIX_DRAFT + value );
    }

    public Branch getMasterBranch()
    {
        return isDefault() ? ContentConstants.BRANCH_MASTER : Branch.from( ContentLayerConstants.BRANCH_PREFIX_MASTER + value );
    }

    public static final class Builder
    {
        private String value;

        private Builder()
        {
        }

        public Builder value( String value )
        {
            this.value = value;
            return this;
        }

        public ContentLayerName build()
        {
            return new ContentLayerName( this );
        }
    }
}


