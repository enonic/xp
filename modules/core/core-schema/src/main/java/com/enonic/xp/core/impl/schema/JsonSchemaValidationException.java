package com.enonic.xp.core.impl.schema;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.networknt.schema.Error;

import com.enonic.xp.exception.BaseException;

public final class JsonSchemaValidationException
    extends BaseException
{
    public JsonSchemaValidationException( final String schemaId, final List<Error> errors )
    {
        super( String.format( "Schema '%s' validation failed:\n%s", schemaId, buildMessage( errors ) ) );
    }

    private static String buildMessage( final List<Error> errors )
    {
        if ( errors == null || errors.isEmpty() )
        {
            return "";
        }

        final Map<String, List<Error>> branched = errors.stream().collect( Collectors.groupingBy( e -> {
            String path = e.getInstanceLocation().toString();
            String[] segments = path.split( "/" );
            return segments.length > 2 ? "/" + segments[1] + "/" + segments[2] : path;
        } ) );

        final List<String> finalOutput = new ArrayList<>();

        branched.forEach( ( branch, branchErrors ) -> {
            int maxDepth = branchErrors.stream().mapToInt( e -> e.getInstanceLocation().toString().split( "/" ).length ).max().orElse( 0 );

            Map<String, Set<Error>> leafs = branchErrors.stream()
                .filter( e -> e.getInstanceLocation().toString().split( "/" ).length == maxDepth )
                .collect( Collectors.groupingBy( e -> e.getInstanceLocation().toString(), LinkedHashMap::new, Collectors.toSet() ) );

            leafs.forEach( ( path, errorSet ) -> {
                List<String> allowed = errorSet.stream()
                    .filter( e -> e.getMessage().contains( "must be the constant value" ) )
                    .map( e -> e.getMessage().replaceAll( ".*'([^']+)'.*", "$1" ) )
                    .distinct()
                    .sorted()
                    .collect( Collectors.toList() );

                if ( !allowed.isEmpty() && path.endsWith( "/type" ) )
                {
                    finalOutput.add( String.format( "[%s] -> Invalid type. Must be one of: %s", path, String.join( ", ", allowed ) ) );
                }
                else
                {
                    String details = errorSet.stream()
                        .map( Error::getMessage )
                        .filter( m -> !m.contains( "must be the constant value" ) && !m.contains( "one and only one" ) &&
                            !m.contains( "is not evaluated" ) )
                        .map( m -> m.replaceAll( "is not defined in the schema.*", "is unknown property" ) )
                        .distinct()
                        .collect( Collectors.joining( "; " ) );

                    if ( !details.isEmpty() )
                    {
                        finalOutput.add( String.format( "[%s] -> %s", path, details ) );
                    }
                }
            } );
        } );

        return " - " + String.join( "\n - ", finalOutput );
    }
}
