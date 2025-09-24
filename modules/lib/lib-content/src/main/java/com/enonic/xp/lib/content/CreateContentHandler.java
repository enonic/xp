package com.enonic.xp.lib.content;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.script.ScriptValue;

import static com.google.common.base.Strings.nullToEmpty;

public final class CreateContentHandler
    extends BaseContentHandler
{
    private String name;

    private String parentPath;

    private String displayName;

    private boolean requireValid = true;

    private boolean refresh = true;

    private Map<String, Object> data;

    private Map<String, Object> page;

    private Map<String, Object> x;

    private String contentType;

    private String language;

    private String childOrder;

    private Supplier<String> idGenerator = () -> Long.toString( ThreadLocalRandom.current().nextLong( Long.MAX_VALUE ) );

    private Map<String, Object> workflow;

    @Override
    protected Object doExecute()
    {
        if ( nullToEmpty( this.name ).isBlank() && !nullToEmpty( this.displayName ).isBlank() && !nullToEmpty( this.parentPath ).isBlank() )
        {
            this.name = generateUniqueContentName( ContentPath.from( this.parentPath ), this.displayName );
        }
        if ( nullToEmpty( this.displayName ).isBlank() && !nullToEmpty( this.name ).isBlank() )
        {
            this.displayName = this.name;
        }

        final CreateContentParams params = createParams();
        final Content result = this.contentService.create( params );
        return new ContentMapper( result );
    }

    private CreateContentParams createParams()
    {
        final ContentTypeName contentTypeName = contentTypeName( this.contentType );

        return CreateContentParams.create().
            name( this.name ).
            parent( contentPath( this.parentPath ) ).
            displayName( this.displayName ).
            requireValid( this.requireValid ).
            type( contentTypeName ).
            contentData( createPropertyTree( data, contentTypeName ) ).
            extraDatas( createExtraDatas( x, contentTypeName ) ).page( createPage( page ) ).
            language( language != null ? Locale.forLanguageTag( language ) : null ).
            childOrder( childOrder != null ? ChildOrder.from( childOrder ) : null ).
            refresh( this.refresh ).
            workflowInfo( createWorkflowInfo( this.workflow ) ).
            build();
    }

    private ContentPath contentPath( final String value )
    {
        return value != null ? ContentPath.from( value ) : null;
    }

    private ContentTypeName contentTypeName( final String value )
    {
        return value != null ? ContentTypeName.from( value ) : null;
    }

    private String generateUniqueContentName( final ContentPath parent, final String displayName )
    {
        final String baseName = NamePrettyfier.create( displayName );

        String name = baseName;
        while ( this.contentService.contentExists( ContentPath.from( parent, name ) ) )
        {
            final String randomId = this.idGenerator.get();
            name = NamePrettyfier.create( baseName + "-" + randomId );
        }

        return name;
    }

    @Override
    protected boolean strictDataValidation()
    {
        return this.requireValid;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setParentPath( final String parentPath )
    {
        this.parentPath = parentPath;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setRequireValid( final Boolean requireValid )
    {
        if ( requireValid != null )
        {
            this.requireValid = requireValid;
        }
    }

    public void setRefresh( final Boolean refresh )
    {
        if ( refresh != null )
        {
            this.refresh = refresh;
        }
    }

    public void setData( final ScriptValue data )
    {
        this.data = data != null ? data.getMap() : null;
    }

    public void setPage( final ScriptValue page )
    {
        this.page = page != null ? page.getMap() : null;
    }

    public void setX( final ScriptValue x )
    {
        this.x = x != null ? x.getMap() : null;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public void setLanguage( final String language )
    {
        this.language = language;
    }

    public void setChildOrder( final String childOrder )
    {
        this.childOrder = childOrder;
    }

    public void setIdGenerator( final Supplier<String> idGenerator )
    {
        if ( idGenerator != null )
        {
            this.idGenerator = idGenerator;
        }
    }

    public void setWorkflow( final ScriptValue workflow )
    {
        if ( workflow != null )
        {
            this.workflow = workflow.getMap();
        }
    }
}
