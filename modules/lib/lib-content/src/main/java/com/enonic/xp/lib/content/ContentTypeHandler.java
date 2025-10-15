package com.enonic.xp.lib.content;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.lib.content.mapper.ContentTypeMapper;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.content.CmsFormFragmentService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ContentTypeHandler
    implements ScriptBean
{
    private Supplier<ContentTypeService> contentTypeService;

    private Supplier<CmsFormFragmentService> mixinService;

    private String name;

    public ContentTypeMapper getContentType()
    {
        if ( name == null || name.isBlank() )
        {
            return null;
        }
        final GetContentTypeParams params = GetContentTypeParams.from( ContentTypeName.from( name ) );
        final ContentType ctype = contentTypeService.get().getByName( params );
        return ctype == null ? null : new ContentTypeMapper( ctype );
    }

    public List<ContentTypeMapper> getAllContentTypes()
    {
        return contentTypeService.get().getAll().stream().map( ContentTypeMapper::new ).collect( Collectors.toList() );
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        contentTypeService = context.getService( ContentTypeService.class );
        mixinService = context.getService( CmsFormFragmentService.class );
    }

}
