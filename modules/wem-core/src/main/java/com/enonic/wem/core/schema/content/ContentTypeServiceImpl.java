package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.CreateContentTypeParams;
import com.enonic.wem.api.schema.content.DeleteContentTypeParams;
import com.enonic.wem.api.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.schema.content.GetChildContentTypesParams;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.api.schema.content.UpdateContentTypeParams;
import com.enonic.wem.api.schema.content.UpdateContentTypeResult;
import com.enonic.wem.api.schema.content.ValidateContentTypeParams;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;

public class ContentTypeServiceImpl
    implements ContentTypeService
{
    @Inject
    private ContentTypeDao contentTypeDao;

    @Inject
    private MixinService mixinService;

    @Override
    public ContentType getByName( final GetContentTypeParams params )
    {
        return new GetContentTypeCommand().
            params( params ).
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypes getByNames( final GetContentTypesParams params )
    {
        return new GetContentTypesCommand().
            params( params ).
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypes getAll( final GetAllContentTypesParams params )
    {
        return new GetAllContentTypesCommand().
            params( params ).
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypes getRoots()
    {
        return new GetRootContentTypesCommand().
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypes getChildren( final GetChildContentTypesParams params )
    {
        return new GetChildContentTypesCommand().
            params( params ).
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentType create( final CreateContentTypeParams params )
    {
        return new CreateContentTypeCommand().
            params( params ).
            contentTypeDao( this.contentTypeDao ).
            contentTypeService( this ).
            execute();
    }

    @Override
    public UpdateContentTypeResult update( final UpdateContentTypeParams params )
    {
        return new UpdateContentTypeCommand().
            params( params ).
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService ).
            contentTypeService( this ).
            execute();
    }

    @Override
    public DeleteContentTypeResult delete( final DeleteContentTypeParams params )
    {
        return new DeleteContentTypeCommand().
            params( params ).
            contentTypeDao( this.contentTypeDao ).
            mixinService( this.mixinService ).
            execute();
    }

    @Override
    public ContentTypeValidationResult validate( final ValidateContentTypeParams params )
    {
        return new ValidateContentTypeCommand().params( params ).contentTypeService( this ).execute();
    }
}
