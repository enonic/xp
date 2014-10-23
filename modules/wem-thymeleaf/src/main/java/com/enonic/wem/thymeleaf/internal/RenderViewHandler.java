package com.enonic.wem.thymeleaf.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.thymeleaf.RenderView;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

@Component(immediate = true)
public final class RenderViewHandler
    implements CommandHandler<RenderView>
{
    private ThymeleafProcessorFactory factory;

    @Override
    public Class<RenderView> getType()
    {
        return RenderView.class;
    }

    @Override
    public RenderView newCommand()
    {
        return new RenderView();
    }

    @Override
    public void invoke( final RenderView command )
    {
        final ThymeleafProcessor processor = this.factory.newProcessor();
        processor.view( command.getView() );
        processor.parameters( command.getParameters() );
        command.setResult( processor.process() );
    }

    @Reference
    public void setFactory( final ThymeleafProcessorFactory factory )
    {
        this.factory = factory;
    }
}
