package com.enonic.xp.lib.content;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.form.Form;
import com.enonic.xp.lib.content.mapper.ContentTypeMapper;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ContentTypeHandler
    implements ScriptBean
{
    private Supplier<ContentTypeService> contentTypeService;

    private Supplier<MixinService> mixinService;

    private String name;

    public ContentTypeMapper getContentType()
    {
        if ( name == null || name.trim().isEmpty() )
        {
            return null;
        }
        final GetContentTypeParams params = GetContentTypeParams.from( ContentTypeName.from( name ) );
        final ContentType ctype = contentTypeService.get().getByName( params );
        return ctype == null ? null : new ContentTypeMapper( inlineMixins( ctype ) );
    }

    public List<ContentTypeMapper> getAllContentTypes()
    {
        final ContentTypes types = contentTypeService.get().getAll();

        return types.stream().map( this::inlineMixins ).map( ContentTypeMapper::new ).collect( Collectors.toList() );
    }

    private ContentType inlineMixins( final ContentType contentType )
    {
        final ContentType.Builder ctInlined = ContentType.create( contentType );
        final Form inlinedForm = mixinService.get().inlineFormItems( contentType.getForm() );
        if ( inlinedForm == null )
        {
            return contentType;
        }
        return ctInlined.form( inlinedForm ).build();
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        contentTypeService = context.getService( ContentTypeService.class );
        mixinService = context.getService( MixinService.class );
    }

}
