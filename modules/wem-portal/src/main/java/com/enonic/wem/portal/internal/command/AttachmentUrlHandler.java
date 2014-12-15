package com.enonic.wem.portal.internal.command;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.url.PortalUrlBuilder;
import com.enonic.wem.portal.url.PortalUrlBuildersHelper;
import com.enonic.wem.script.command.CommandHandler;

@Component(immediate = true, service = CommandHandler.class)
public final class AttachmentUrlHandler
    extends AbstractUrlHandler
{
    public AttachmentUrlHandler()
    {
        super( "attachmentUrl" );
    }

    @Override
    protected PortalUrlBuilder createBuilder( final Multimap<String, String> map )
    {
        return PortalUrlBuildersHelper.apply( createBuilders().attachmentUrl(), map );
    }
}
