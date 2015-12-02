package com.enonic.xp.core.impl.schema.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.ContentTypeValidationResult;
import com.enonic.xp.schema.mixin.MixinService;

@Component(immediate = true)
public final class ContentTypeServiceImpl
    implements ContentTypeService, ApplicationInvalidator
{
    private final ContentTypeRegistryImpl registry;

    private MixinService mixinService;

    public ContentTypeServiceImpl()
    {
        this.registry = new ContentTypeRegistryImpl();
    }

    @Override
    public ContentType getByName( final GetContentTypeParams params )
    {
        final GetContentTypeCommand command = new GetContentTypeCommand();
        command.registry = this.registry;
        command.mixinService = this.mixinService;
        command.params = params;
        return command.execute();
    }

    @Override
    public ContentTypes getByApplication( final ApplicationKey applicationKey )
    {
        return this.registry.getByApplication( applicationKey );
    }

    @Override
    public ContentTypes getAll( final GetAllContentTypesParams params )
    {
        final GetAllContentTypesCommand command = new GetAllContentTypesCommand();
        command.registry = this.registry;
        command.mixinService = this.mixinService;
        command.params = params;
        return command.execute();
    }

    @Override
    public ContentTypeValidationResult validate( final ContentType type )
    {
        final ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.
            create().
            contentTypeService( this ).
            build();

        validator.validate( type.getName(), type.getSuperType() );
        return validator.getResult();
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.registry.resourceService = resourceService;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.registry.applicationService = applicationService;
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.registry.invalidate();
    }
}
