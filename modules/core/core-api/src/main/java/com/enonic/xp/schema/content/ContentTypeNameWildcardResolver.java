package com.enonic.xp.schema.content;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.codehaus.jparsec.util.Lists;

public class ContentTypeNameWildcardResolver
{
    public static final String APP_WILDCARD = "${app}";

    public static final String ANY_WILDCARD = "*";

    private final ContentTypeService contentTypeService;

    public ContentTypeNameWildcardResolver( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public boolean anyTypeHasWildcard( final List<String> contentTypeNames )
    {
        return contentTypeNames.stream().anyMatch( s -> this.hasAnyWildcard( s ) || this.hasAppWildcard( s ) );
    }

    private boolean hasAppWildcard( String s )
    {
        return s.startsWith( APP_WILDCARD );
    }

    private boolean hasAnyWildcard( String s )
    {
        return s.contains( ANY_WILDCARD );
    }

    public List<String> resolveWildcards( final List<String> namesToResolve, final ContentTypeName contentTypeName )
    {
        List<String> allContentTypes = contentTypeService.
            getAll( new GetAllContentTypesParams().inlineMixinsToFormItems( false ) ).
            stream().
            map( type -> type.getName().toString() ).
            collect( Collectors.toList() );

        List<String> resolvedNames = Lists.arrayList();

        namesToResolve.forEach( name -> {
            if ( this.hasAnyWildcard( name ) || this.hasAppWildcard( name ) )
            {
                String resolvedName;
                if ( this.hasAppWildcard( name ) )
                {
                    resolvedName = this.resolveAppWildcard( name, contentTypeName );
                }
                else
                {
                    resolvedName = name;
                }
                if ( this.hasAnyWildcard( resolvedName ) )
                {
                    resolvedNames.addAll( this.resolveAnyWildcard( resolvedName, allContentTypes ) );
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

    private List<String> resolveAnyWildcard( final String nameToResolve, final List<String> allContentTypes )
    {
        Predicate<String> pattern = Pattern.compile( nameToResolve.replaceAll( "\\*", ".*" ) ).asPredicate();
        return allContentTypes.stream().filter( pattern ).collect( Collectors.toList() );
    }

    private String resolveAppWildcard( final String nameToResolve, final ContentTypeName contentTypeName )
    {
        return nameToResolve.replace( APP_WILDCARD, contentTypeName.getApplicationKey().toString() );
    }
}
