package com.enonic.xp.core.impl.schema.content;

import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.schema.YmlContentTypeParser;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeFromMimeTypeResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.validator.ContentTypeValidationResult;
import com.enonic.xp.schema.mixin.MixinService;

@Component(immediate = true)
public final class ContentTypeServiceImpl
    implements ContentTypeService
{
    private static final YmlContentTypeParser PARSER = new YmlContentTypeParser();

    private final ContentTypeRegistry registry;

    private final MixinService mixinService;

    @Activate
    public ContentTypeServiceImpl( final @Reference ResourceService resourceService, @Reference final ApplicationService applicationService,
                                   final @Reference MixinService mixinService )
    {
        this.registry = new ContentTypeRegistry( new ContentTypeLoader( resourceService, this ), applicationService );
        this.mixinService = mixinService;
    }

    @Override
    public ContentType getByName( final GetContentTypeParams params )
    {
        params.validate();
        final ContentTypeName contentTypeName = params.getContentTypeName();
        ContentType contentType = this.registry.get( contentTypeName );
        if ( contentType == null )
        {
            ContentTypeName ymlContentType =
                ContentTypeName.from( contentTypeName.getApplicationKey(), contentTypeName.getLocalName(), "yml" );
            contentType = this.registry.get( ymlContentType );
            if ( contentType == null )
            {
                return null;
            }
        }

        return transformInlineMixins( contentType );
    }

    @Override
    public ContentTypes getByApplication( final ApplicationKey applicationKey )
    {
        return transformInlineMixins( this.registry.getByApplication( applicationKey ) );
    }

    @Override
    public ContentTypes getAll()
    {
        return transformInlineMixins( this.registry.getAll() );
    }

    @Override
    public Set<String> getMimeTypes( final ContentTypeNames names )
    {
        return ContentTypeFromMimeTypeResolver.resolveMimeTypes( names );
    }

    @Override
    public ContentTypeValidationResult validate( final ContentType type )
    {
        final ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.create().contentTypeService( this ).build();

        validator.validate( type.getName(), type.getSuperType() );
        return validator.getResult();
    }

    @Override
    public ContentType.Builder createContentTypeFromYml( final String contentTypeYml, final ApplicationKey applicationKey )
    {
        return PARSER.parse( contentTypeYml, applicationKey );
    }

    private ContentType transformInlineMixins( final ContentType contentType )
    {
        return ContentType.create( contentType ).form( mixinService.inlineFormItems( contentType.getForm() ) ).build();
    }

    private ContentTypes transformInlineMixins( final ContentTypes contentTypes )
    {
        return contentTypes.stream().map( this::transformInlineMixins ).collect( ContentTypes.collector() );
    }
}
