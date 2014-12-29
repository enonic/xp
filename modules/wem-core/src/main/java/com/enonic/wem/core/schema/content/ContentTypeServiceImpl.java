package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.content.ValidateContentTypeParams;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.mixin.MixinService;

public class ContentTypeServiceImpl
    implements ContentTypeService
{
    private ContentTypeRegistry registry;

    private MixinService mixinService;

    @Override
    public ContentType getByName( final GetContentTypeParams params )
    {
        return new GetContentTypeCommand().
            params( params ).
            registry( this.registry ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypes getByNames( final GetContentTypesParams params )
    {
        return new GetContentTypesCommand().
            params( params ).
            registry( this.registry ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypes getByModule( final ModuleKey moduleKey )
    {
        return this.registry.getContentTypesByModule( moduleKey );
    }

    @Override
    public ContentTypes getAll( final GetAllContentTypesParams params )
    {
        return new GetAllContentTypesCommand().
            params( params ).
            registry( this.registry ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypes getRoots()
    {
        return new GetRootContentTypesCommand().
            registry( this.registry ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypes getChildren( final GetChildContentTypesParams params )
    {
        return new GetChildContentTypesCommand().
            params( params ).
            registry( this.registry ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypeValidationResult validate( final ValidateContentTypeParams params )
    {
        return new ValidateContentTypeCommand().params( params ).contentTypeService( this ).execute();
    }

    public void setRegistry( final ContentTypeRegistry registry )
    {
        this.registry = registry;
    }

    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}
