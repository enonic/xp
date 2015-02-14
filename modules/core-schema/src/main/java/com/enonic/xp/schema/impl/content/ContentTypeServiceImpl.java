package com.enonic.xp.schema.impl.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeProvider;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.mixin.MixinService;

@Component(immediate = true)
public final class ContentTypeServiceImpl
    implements ContentTypeService
{
    private final ContentTypeRegistry registry;

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
    public ContentTypes getByNames( final GetContentTypesParams params )
    {
        final GetContentTypesCommand command = new GetContentTypesCommand();
        command.registry = this.registry;
        command.mixinService = this.mixinService;
        command.params = params;
        return command.execute();
    }

    @Override
    public ContentTypes getByModule( final ModuleKey moduleKey )
    {
        return this.registry.getByModule( moduleKey );
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
    public ContentTypes getChildren( final GetChildContentTypesParams params )
    {
        final GetChildContentTypesCommand command = new GetChildContentTypesCommand();
        command.registry = this.registry;
        command.mixinService = this.mixinService;
        command.params = params;
        return command.execute();
    }

    @Override
    public ContentTypeValidationResult validate( final ContentType type )
    {
        final ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.
            newContentTypeSuperTypeValidator().
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

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addProvider( final ContentTypeProvider provider )
    {
        this.registry.addProvider( provider );
    }

    public void removeProvider( final ContentTypeProvider provider )
    {
        this.registry.removeProvider( provider );
    }
}
