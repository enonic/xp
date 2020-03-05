package com.enonic.xp.project.layer;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.project.ProjectName;

@PublicApi
public final class ContentLayerKey
{
    private final static String SEPARATOR = ":";

    private final static String VALID_KEY_REGEX =
        "(" + ProjectName.VALID_PROJECT_NAME_REGEX + ")" + SEPARATOR + "(" + ContentLayerName.VALID_NAME_REGEX + ")";

    private final static Pattern VALID_KEY_PATTERN = Pattern.compile( VALID_KEY_REGEX );

    private final ProjectName projectName;

    private final ContentLayerName layerName;

    private ContentLayerKey( final Builder builder )
    {
        Preconditions.checkArgument( builder.layerName != null, "Layer name cannot be null or empty" );
        Preconditions.checkArgument( builder.projectName != null, "Project name cannot be null or empty" );

        this.projectName = builder.projectName;
        this.layerName = builder.layerName;
    }

    public static ContentLayerKey from( final String key )
    {
        final Matcher matcher = VALID_KEY_PATTERN.matcher( key );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid content layer key [" + key + "]" );
        }

        final ProjectName projectName = ProjectName.from( matcher.group( 1 ) );
        final ContentLayerName layerName = ContentLayerName.from( matcher.group( 2 ) );

        return from( projectName, layerName );
    }

    public static ContentLayerKey from( final ProjectName projectName, final ContentLayerName contentLayerName )
    {
        return ContentLayerKey.create().
            projectName( projectName ).
            layerName( contentLayerName ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public ContentLayerName getLayerName()
    {
        return layerName;
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
        final ContentLayerKey that = (ContentLayerKey) o;
        return Objects.equals( projectName, that.projectName ) && Objects.equals( layerName, that.layerName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( projectName, layerName );
    }

    @Override
    public String toString()
    {
        return projectName + SEPARATOR + layerName;
    }


    public static final class Builder
    {
        private ProjectName projectName;

        private ContentLayerName layerName;

        private Builder()
        {
        }

        public Builder layerName( ContentLayerName value )
        {
            this.layerName = value;
            return this;
        }

        public Builder projectName( ProjectName value )
        {
            this.projectName = value;
            return this;
        }

        public ContentLayerKey build()
        {
            return new ContentLayerKey( this );
        }
    }
}
