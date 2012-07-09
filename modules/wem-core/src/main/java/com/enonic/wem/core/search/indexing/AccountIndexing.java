package com.enonic.wem.core.search.indexing;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.time.StopWatch;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.account.Account;
import com.enonic.wem.core.search.account.AccountIndexData;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.core.search.account.Group;
import com.enonic.wem.core.search.account.User;

@Component
@DependsOn("jcrBootstrap")
public class AccountIndexing
    implements InitializingBean, Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountIndexing.class );

    private final static int BATCH_SIZE = 20;

    private AccountSearchService accountSearchService;

    private BatchLoader<User> userLoader;

    private BatchLoader<Group> groupLoader;

    private final StopWatch stopWatch;

    private final AtomicBoolean running;

    private final AtomicInteger contentTotal;

    private final AtomicInteger progressCount;

    private ExecutorService executorService;

    AccountIndexing()
    {
        stopWatch = new StopWatch();
        running = new AtomicBoolean( false );
        contentTotal = new AtomicInteger( -1 );
        progressCount = new AtomicInteger( 0 );
        executorService = Executors.newSingleThreadExecutor();
    }

    public void afterPropertiesSet()
        throws Exception
    {
        userLoader.setBatchSize( BATCH_SIZE );
        groupLoader.setBatchSize( BATCH_SIZE );
        autoIndex();
    }

    public boolean isRunning()
    {
        return running.get();
    }

    public int getProgress()
    {
        int total = contentTotal.get();
        if (total <= 0) {
            return 0;
        }

        return Math.round( ( (float) getProgressCount() / (float) total ) * 100f );
    }

    private int getProgressCount()
    {
        return progressCount.get();
    }

    private void autoIndex()
    {
//        if ( !accountSearchService.indexExists() )
//        {
//            LOG.info( "Accounts index not found, initializing indexes." );
            indexAccounts();
//        }
    }

    public void indexAccounts()
    {
        if ( !running.compareAndSet( false, true ) )
        {
            LOG.warn( "Accounts indexing already in process" );
            return;
        }

        executorService.submit( this );
    }

    @Override
    public void run()
    {
        doIndexAccounts();
    }

    private void doIndexAccounts()
    {
        try
        {
            startIndexing();

            initializeIndexAndMapping();

            indexUsers();

            indexGroups();

            progressCount.set( contentTotal.get() );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to index accounts.", e );
        }
        finally
        {
            indexingCompleted();
        }
    }

    private void startIndexing()
    {
        LOG.info( "Starting indexing of accounts." );
        stopWatch.reset();
        stopWatch.start();

        contentTotal.set( groupLoader.getTotal() + userLoader.getTotal() );
        progressCount.set( 0 );
    }

    private void indexingCompleted()
    {
        stopWatch.stop();
        LOG.info( "Account indexing completed. Execution time " + toDurationString( stopWatch.getTime() ) );

        running.set( false );
    }

    private void indexGroups()
    {
        LOG.info( "Indexing account groups..." );

        groupLoader.reset();
        final int total = groupLoader.getTotal();
        int count = 0;

        while ( groupLoader.hasNext() )
        {
            final List<Group> groups = groupLoader.next();
            for ( Account group : groups )
            {
                count++;
                logIndexedAccount( group, count, total );
                progressCount.incrementAndGet();

                AccountIndexData accountIndexData = new AccountIndexData( group );
                accountSearchService.index( accountIndexData );
            }
        }
        LOG.info( "Account groups indexed." );
    }

    private void indexUsers()
    {
        LOG.info( "Indexing account users..." );

        userLoader.reset();
        final int total = userLoader.getTotal();
        int count = 0;

        while ( userLoader.hasNext() )
        {
            final List<User> users = userLoader.next();
            for ( User user : users )
            {
                count++;
                logIndexedAccount( user, count, total );
                progressCount.incrementAndGet();

                AccountIndexData accountIndexData = new AccountIndexData( user );
                accountSearchService.index( accountIndexData );
            }
        }
        LOG.info( "Account users indexed." );
    }

    private void logIndexedAccount( Account account, int count, int total )
    {
        final String progress = String.format( "% 4d", count ) + "/" + total;
        LOG.info( "Indexing " + account.getType().name().toLowerCase() + " " + progress + " [" + account.getQualifiedName() + "]" );
    }

    private void initializeIndexAndMapping()
    {
        LOG.info( "Creating index and mapping for accounts." );
        accountSearchService.dropIndex();
        accountSearchService.createIndex();
    }

    private String toDurationString(long time)
    {
        PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
           .appendDays()
           .appendSuffix( " day", " days" )
           .appendSeparator( " and " )
           .appendMinutes()
           .appendSuffix( " minute", " minutes" )
           .appendSeparator( " and " )
           .appendSeconds()
           .appendSuffix( " second", " seconds" )
           .toFormatter();

        Period period = new Period( time, PeriodType.seconds() );
        return daysHoursMinutes.print( period );
    }

    @Autowired
    public void setAccountSearchService( AccountSearchService accountSearchService )
    {
        this.accountSearchService = accountSearchService;
    }

    @Autowired
    @Qualifier("jcrGroupLoader")
    public void setGroupLoader( BatchLoader<Group> groupLoader )
    {
        this.groupLoader = groupLoader;
    }

    @Autowired
    @Qualifier("jcrUserLoader")
    public void setUserLoader( BatchLoader<User> userLoader )
    {
        this.userLoader = userLoader;
    }

}
