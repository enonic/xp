package com.enonic.xp.lib.repo;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.lib.repo.mapper.RepositoryMapper;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.repository.EditableRepository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

@SuppressWarnings("unused")
public class UpdateRepositoryHandler
    implements ScriptBean
{
    private String id;

    private ScriptValue editor;

    private String scope;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setScope( final String scope )
    {
        this.scope = scope;
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }

    public RepositoryMapper execute()
    {
        final RepositoryId repositoryId = RepositoryId.from( id );

        final UpdateRepositoryParams updateRepositoryParams = UpdateRepositoryParams.create().
            repositoryId( repositoryId ).
            editor( newRepositoryEditor() ).
            build();
        return new RepositoryMapper( repositoryServiceSupplier.get().updateRepository( updateRepositoryParams ) );
    }

    private Consumer<EditableRepository> newRepositoryEditor()
    {
        return edit -> {
            final ScriptValue value = this.editor.call( new RepositoryMapper( edit.source ) );
            updateRepositoryData( edit, value );
            edit.binaryAttachments = new RepositoryBinaryAttachmentsParser().parse( value );
        };
    }

    private void updateRepositoryData( final EditableRepository target, final ScriptValue value )
    {
        if ( value == null )
        {
            if ( scope != null )
            {
                target.data.getValue( scope );
            }
            return;
        }
        final ScriptValueTranslatorResult scriptValueTranslatorResult = new ScriptValueTranslator( false ).create( value );
        final PropertyTree propertyTree = scriptValueTranslatorResult.getPropertyTree();

        if ( this.scope == null )
        {
            target.data = propertyTree;
        }
        else
        {
            target.data.setSet( scope, propertyTree.getRoot() );
        }
    }

    private PropertyTreeMapper createPropertyTreeMapper( PropertyTree profile, Boolean useRawValue )
    {
        if ( profile == null )
        {
            return null;
        }

        if ( this.scope == null )
        {
            return new PropertyTreeMapper( useRawValue, profile );
        }
        else
        {
            final PropertySet scopedProfile = profile.getSet( scope );
            return scopedProfile == null ? null : new PropertyTreeMapper( useRawValue, scopedProfile.toTree() );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
