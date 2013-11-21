package com.enonic.wem.core.content.site;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.site.DeleteSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static junit.framework.Assert.assertNull;

public class DeleteSiteHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteSiteHandler handler;

    @Before
    public void before()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new DeleteSiteHandler();
        handler.setContext( this.context );
    }

    @Test
    public void delete_site()
        throws Exception
    {
        DeleteSite command = new DeleteSite().content( ContentId.from( "1" ) );

        final Site.Builder siteBuilder = new Site.Builder();
        final Site site = siteBuilder.build();

        final Content.Builder contentBuilder = Content.newContent();
        contentBuilder.site( site );
        final Content toBeEdited = contentBuilder.build();

        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenAnswer( new Answer<UpdateContent>() {
            public UpdateContent answer(InvocationOnMock invocation) throws Throwable {
                UpdateContent updateContent = ( UpdateContent ) invocation.getArguments()[0];
                final Content.EditBuilder builder = updateContent.getEditor().edit( toBeEdited );

                final Content content = builder.build();
                assertNull( content.getSite() );

                return updateContent;
            }
        } );

        // exercise
        this.handler.setCommand( command );
        handler.handle();
    }
}
