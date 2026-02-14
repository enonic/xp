package com.enonic.xp.lib.content.deserializer;

import java.util.Map;
import java.util.function.Function;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;

public final class PropertyTreeTranslator
{
    private final ContentTypeService contentTypeService;

    private final MixinService mixinService;

    private final CmsService cmsService;

    private final PropertyTreeMarshallerService marshaller;

    private final Function<ContentTypeName, Boolean> strictValidationResolver;

    public PropertyTreeTranslator( ContentTypeService contentTypeService, MixinService mixinService, CmsService cmsService,
                                   PropertyTreeMarshallerService marshaller, Function<ContentTypeName, Boolean> strictValidationResolver )
    {
        this.contentTypeService = contentTypeService;
        this.mixinService = mixinService;
        this.cmsService = cmsService;
        this.marshaller = marshaller;
        this.strictValidationResolver = strictValidationResolver;
    }

    public PropertyTree translate( Map<String, Object> map )
    {
        return marshaller.marshal( map, Form.create().build(), false );
    }

    public PropertyTree translate( Map<String, Object> map, ContentTypeName contentTypeName )
    {
        ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( contentTypeName ) );
        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Unknown content type: " + contentTypeName );
        }
        return marshaller.marshal( map, contentType.getForm(), strictValidationResolver.apply( contentTypeName ) );
    }

    public PropertyTree translate( Map<String, Object> map, MixinName mixinName, ContentTypeName contentTypeName )
    {
        MixinDescriptor mixinDescriptor = mixinService.getByName( mixinName );
        if ( mixinDescriptor == null )
        {
            throw new IllegalArgumentException( "Unknown mixin: " + mixinName );
        }
        return marshaller.marshal( map, mixinDescriptor.getForm(), strictValidationResolver.apply( contentTypeName ) );
    }

    public PropertyTree translate( Map<String, Object> map, ApplicationKey appKey, ContentTypeName contentTypeName )
    {
        CmsDescriptor descriptor = cmsService.getDescriptor( appKey );
        if ( descriptor == null )
        {
            throw new IllegalArgumentException( "Unknown site descriptor: " + appKey );
        }
        Form form = descriptor.getForm();
        return marshaller.marshal( map, form, strictValidationResolver.apply( contentTypeName ) );
    }
}
