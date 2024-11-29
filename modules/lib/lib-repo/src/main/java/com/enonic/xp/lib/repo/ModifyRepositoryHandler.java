package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.PropertyTree;
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
public class ModifyRepositoryHandler
    implements ScriptBean
{
    private String id;

    private ScriptValue editor;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setId( final String id )
    {
        this.id = id;
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
            editor( this::editRepository ).
            build();
        return new RepositoryMapper( repositoryServiceSupplier.get().updateRepository( updateRepositoryParams ) );
    }

    private void editRepository( EditableRepository target )
    {
        final ScriptValue value = this.editor.call( new RepositoryMapper( target.source ) );
        updateRepositoryData( target, value );
    }

    private void updateRepositoryData( final EditableRepository target, final ScriptValue value )
    {
        if ( value != null )
        {
            final ScriptValueTranslatorResult scriptValueTranslatorResult = new ScriptValueTranslator().create( value );
            target.binaryAttachments = ImmutableList.copyOf( scriptValueTranslatorResult.getBinaryAttachments() );

            final PropertyTree propertyTree = scriptValueTranslatorResult.getPropertyTree();
            target.data = propertyTree.getRoot().getSet( "data" ).toTree();
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
