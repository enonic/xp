package com.enonic.wem.thymeleaf.internal;

import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.thymeleaf.RenderView;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

public final class RenderViewHandler
    implements CommandHandler<RenderView>
{
    private final ThymeleafProcessorFactory thymeleafProcessorFactory;

    public RenderViewHandler( final ThymeleafProcessorFactory thymeleafProcessorFactory )
    {
        this.thymeleafProcessorFactory = thymeleafProcessorFactory;
    }

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
        final ThymeleafProcessor processor = this.thymeleafProcessorFactory.newProcessor();
        processor.view( command.getView() );
        processor.parameters( command.getParameters() );
        command.setResult( processor.process() );
    }
}
