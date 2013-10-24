package com.enonic.wem.core.command;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;

import static org.junit.Assert.*;

public class CommandContextFactoryImplTest
{
    private Session session;

    private CommandContextFactoryImpl factory;

    private JcrSessionProvider sessionProvider;

    @Before
    public void setup()
        throws Exception
    {
        this.session = Mockito.mock( Session.class );

        this.sessionProvider = Mockito.mock( JcrSessionProvider.class );
        Mockito.when( this.sessionProvider.login() ).thenReturn( this.session );

        this.factory = new CommandContextFactoryImpl();
        this.factory.setJcrSessionProvider( this.sessionProvider );
    }

    @Test
    public void testCreate()
    {
        final CommandContext context = this.factory.create();
        assertNotNull( context );
        assertSame( this.session, context.getJcrSession() );
    }

    @Test
    public void testCreate_runtimeException()
        throws Exception
    {
        final RuntimeException error = new RuntimeException( "error" );
        Mockito.when( this.sessionProvider.login() ).thenThrow( error );

        try
        {
            this.factory.create();
            fail( "Expected exception" );
        }
        catch ( final Exception e )
        {
            assertSame( error, e );
        }
    }

    @Test
    public void testCreate_checkedException()
        throws Exception
    {
        final Exception error = new Exception( "error" );
        Mockito.when( this.sessionProvider.login() ).thenThrow( error );

        try
        {
            this.factory.create();
            fail( "Expected exception" );
        }
        catch ( final Exception e )
        {
            assertSame( SystemException.class, e.getClass() );
            assertSame( error, e.getCause() );
        }
    }
}
