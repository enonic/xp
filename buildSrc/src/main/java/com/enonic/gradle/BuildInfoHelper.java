package com.enonic.gradle;

import java.io.File;
import java.util.Map;

import org.codehaus.groovy.util.ListHashMap;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevWalk;

public final class BuildInfoHelper
{
    private static final String NA = "N/A";

    public static Map<String, String> extract( final File baseDir )
        throws Exception
    {
        final Map<String, String> info = new ListHashMap<>();

        final Repository repository = new RepositoryBuilder().findGitDir( baseDir ).readEnvironment().build();

        info.put( "branch", repository.getBranch() );

        final ObjectId revision = repository.resolve( Constants.HEAD );
        info.put( "hash", revision != null ? revision.getName() : NA );
        info.put( "shortHash", revision != null ? revision.abbreviate( 7 ).name() : NA );
        info.put( "timestamp", revision != null
            ? new RevWalk( repository ).parseCommit( revision ).getCommitterIdent().getWhenAsInstant().toString()
            : NA );
        return info;
    }
}

