package com.enonic.xp.core.impl.schema.content;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeFromMimeTypeResolver;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.ContentTypeValidationResult;
import com.enonic.xp.schema.mixin.MixinService;

@Component(immediate = true)
public final class ContentTypeServiceImpl
    implements ContentTypeService
{
    private final ContentTypeRegistry registry;

    private MixinService mixinService;

    public ContentTypeServiceImpl()
    {
        this.registry = new ContentTypeRegistry();
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
        final GetApplicationContentTypesCommand command = new GetApplicationContentTypesCommand();
        command.registry = this.registry;
        command.mixinService = this.mixinService;
        command.applicationKey = applicationKey;
        command.inlineMixinsToFormItems = true;
        return command.execute();
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
    public Set<String> getMimeTypes( final ContentTypeNames names )
    {
        return ContentTypeFromMimeTypeResolver.resolveMimeTypes( names );
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
}
