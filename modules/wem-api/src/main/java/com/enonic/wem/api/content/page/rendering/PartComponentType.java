package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.template.GetTemplate;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.Part;
import com.enonic.wem.api.content.page.PartDescriptor;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.TemplateId;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.rendering.ComponentType;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;
import com.enonic.wem.api.resource.Resource;

public class PartComponentType
    extends ComponentType<Part>
{
    @Override
    public RenderingResult execute( final Part part, final Context context, final Client client )
    {
//        PartTemplate template = getPartTemplate( part.getTemplateId(), client );
//
//        PartDescriptor descriptor = getPartDescriptor( template.getDescriptor() );
//
//        Resource controllerResource = getControllerResource( descriptor ,client);
//
//        Controller controller = ControllerFactory.create( controllerResource, descriptor.getControllerSetup().getParams() );
//
//        final RootDataSet pageConfig = part.getConfig();
//        final RootDataSet pageConfigFromTemplate = template.getPartConfig();
//        final RootDataSet templateConfig = template.getTemplateConfig();
//        final RootDataSet mergedPageConfig = mergeConfig( pageConfig, pageConfigFromTemplate );
//
//        return new ControllerExecutor( controller, mergedPageConfig, templateConfig ).execute();
        return null;
    }

    private PartTemplate getPartTemplate( final TemplateId templateId, final Client client  )
    {
        final GetTemplate command = Commands.partTemplate().get().byId( templateId );
        return (PartTemplate) client.execute( command );
    }
}
