package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationWildcardResolver;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetAllContentTypesParams;

public class ContentTypeNameWildcardResolver
{
    private final ContentTypeService contentTypeService;

    private final ApplicationWildcardResolver applicationWildcardResolver;

    public ContentTypeNameWildcardResolver( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        this.applicationWildcardResolver = new ApplicationWildcardResolver();
    }

    public boolean anyTypeHasWildcard( final List<String> contentTypeNames )
    {
        return contentTypeNames.stream().anyMatch(
            s -> this.applicationWildcardResolver.hasAnyWildcard( s ) || this.applicationWildcardResolver.startWithAppWildcard( s ) );
    }

    public List<String> resolveContentTypeName( final String regex )
    {

        final List<String> allContentTypes = contentTypeService.
            getAll( new GetAllContentTypesParams().inlineMixinsToFormItems( false ) ).
            stream().
            map( type -> type.getName().toString() ).
            collect( Collectors.toList() );

        final Predicate<String> pattern = Pattern.compile( regex ).asPredicate();

        return allContentTypes.
            stream().
            filter( pattern ).
            collect( Collectors.toList() );
    }

    public List<String> resolveWildcards( final List<String> namesToResolve, final ApplicationKey currentApplicationKey )
    {
        final List<String> resolvedNames = Lists.newArrayList();

        namesToResolve.forEach( name -> {
            if ( this.applicationWildcardResolver.hasAnyWildcard( name ) || this.applicationWildcardResolver.startWithAppWildcard( name ) )
            {
                String resolvedName;
                if ( this.applicationWildcardResolver.startWithAppWildcard( name ) )
                {
                    resolvedName = this.applicationWildcardResolver.resolveAppWildcard( name, currentApplicationKey );
                }
                else
                {
                    resolvedName = name;
                }
                if ( this.applicationWildcardResolver.hasAnyWildcard( resolvedName ) )
                {
                    resolvedNames.addAll( this.resolveAnyWildcard( resolvedName ) );
                }
                else
                {
                    resolvedNames.add( resolvedName );
                }
            }
            else
            {
                resolvedNames.add( name );
            }
        } );

        return resolvedNames;
    }

    private List<String> resolveAnyWildcard( final String nameToResolve )
    {
        return resolveContentTypeName( nameToResolve.replaceAll( "\\*", ".*" ) );
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
        final ContentTypeNameWildcardResolver that = (ContentTypeNameWildcardResolver) o;
        return Objects.equals( contentTypeService, that.contentTypeService );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( contentTypeService );
    }
}
