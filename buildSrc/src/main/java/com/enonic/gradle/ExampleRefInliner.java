package com.enonic.gradle;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ExampleRefInliner
{
    private static final Pattern EXAMPLE_REF = Pattern.compile( "^(\\s*\\*\\s?)@example-ref\\s+(\\S+)\\s*$" );

    private static final String BEGIN = "// BEGIN";

    private static final String END = "// END";

    private ExampleRefInliner()
    {
    }

    public static String inline( final String line, final File baseDir )
    {
        final Matcher matcher = EXAMPLE_REF.matcher( line );
        if ( !matcher.matches() )
        {
            return line;
        }

        final String prefix = matcher.group( 1 );
        final Path file = baseDir.toPath().resolve( matcher.group( 2 ) );
        final List<List<String>> examples = extract( readLines( file ) );
        if ( examples.isEmpty() )
        {
            throw new IllegalStateException( "No '" + BEGIN + "' ... '" + END + "' block in " + file );
        }

        final List<String> result = new ArrayList<>();
        for ( final List<String> example : examples )
        {
            for ( final String exampleLine : example )
            {
                final String escaped = exampleLine.replace( "*/", "*\\/" );
                result.add( escaped.isBlank() ? prefix.stripTrailing() : prefix + escaped );
            }
        }
        return String.join( "\n", result );
    }

    private static List<List<String>> extract( final List<String> lines )
    {
        final List<List<String>> examples = new ArrayList<>();
        List<String> current = null;
        for ( final String line : lines )
        {
            if ( line.startsWith( BEGIN ) )
            {
                current = new ArrayList<>();
                final String caption = line.substring( BEGIN.length() ).trim();
                current.add( caption.isEmpty() ? "@example" : "@example <caption>" + caption + "</caption>" );
            }
            else if ( line.startsWith( END ) )
            {
                if ( current != null )
                {
                    while ( current.size() > 1 && current.getLast().isBlank() )
                    {
                        current.removeLast();
                    }
                    examples.add( current );
                }
                current = null;
            }
            else if ( current != null && !( current.size() == 1 && line.isBlank() ) )
            {
                current.add( line );
            }
        }
        return examples;
    }

    private static List<String> readLines( final Path file )
    {
        try
        {
            return Files.readAllLines( file, StandardCharsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }
}
