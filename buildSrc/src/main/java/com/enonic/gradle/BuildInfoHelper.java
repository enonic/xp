package com.enonic.gradle;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public final class BuildInfoHelper
{
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ssZ" );

    public static BuildInfo extract( final File baseDir )
        throws Exception
    {

        final BuildInfo info = new BuildInfo();

        final RepositoryBuilder builder = new RepositoryBuilder();
        final Repository repository = builder.findGitDir( baseDir ).readEnvironment().build();
        info.setBranch( repository.getBranch() );

        final ObjectId revision = repository.resolve( Constants.HEAD );
        if ( revision == null )
        {
            return info;
        }

        info.setHash( revision.getName() );
        info.setShortHash( revision.abbreviate( 7 ).name() );
        info.setTimestamp( findLastCommitTimestamp( repository, revision ) );
        return info;
    }

    private static String findLastCommitTimestamp( final Repository repository, final ObjectId revision )
        throws Exception
    {
        final RevWalk rw = new RevWalk( repository );
        final RevCommit commit = rw.parseCommit( revision );
        final PersonIdent author = commit.getAuthorIdent();
        final Date commitDate = author.getWhen();

        return DATE_FORMAT.format( commitDate );
    }
}

