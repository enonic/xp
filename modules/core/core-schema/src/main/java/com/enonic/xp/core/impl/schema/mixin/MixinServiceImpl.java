package com.enonic.xp.core.impl.schema.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;

@Component(immediate = true)
public final class MixinServiceImpl
    implements MixinService
{
    private final BuiltinMixinsTypes builtInTypes;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    public MixinServiceImpl()
    {
        this.builtInTypes = new BuiltinMixinsTypes();
    }

    @Override
    public Mixin getByName( final MixinName name )
    {
        if ( isSystem( name ) )
        {
            return this.builtInTypes.getAll().getMixin( name );
        }

        return new MixinLoader( this.resourceService ).get( name );
    }

    private boolean isSystem( final MixinName name )
    {
        return SchemaHelper.isSystem( name.getApplicationKey() );
    }

    @Override
    public Mixins getAll()
    {
        final Set<Mixin> list = Sets.newLinkedHashSet();
        list.addAll( this.builtInTypes.getAll().getList() );

        for ( final Application application : this.applicationService.getAllApplications() )
        {
            final Mixins types = getByApplication( application.getKey() );
            list.addAll( types.getList() );
        }

        return Mixins.from( list );
    }

    @Override
    public Mixins getByApplication( final ApplicationKey key )
    {
        if ( SchemaHelper.isSystem( key ) )
        {
            return this.builtInTypes.getByApplication( key );
        }

        final List<Mixin> list = Lists.newArrayList();
        for ( final MixinName name : findNames( key ) )
        {
            final Mixin type = getByName( name );
            if ( type != null )
            {
                list.add( type );
            }

        }

        return Mixins.from( list );
    }

    private Set<MixinName> findNames( final ApplicationKey key )
    {
        return new MixinLoader( this.resourceService ).findNames( key );
    }

    @Override
    public Mixins getByContentType( final ContentType contentType )
    {
        return Mixins.from( contentType.getMetadata().stream().
            map( this::getByName ).
            filter( Objects::nonNull ).
            collect( Collectors.toSet() ) );
    }

    @Override
    public Form inlineFormItems( final Form form )
    {
        final Form.Builder transformedForm = Form.create();
        final List<FormItem> transformedFormItems = transformFormItems( form );
        transformedFormItems.forEach( transformedForm::addFormItem );
        return transformedForm.build();
    }

    private List<FormItem> transformFormItems( final Iterable<FormItem> iterable )
    {
        final List<FormItem> formItems = new ArrayList<>();
        for ( final FormItem formItem : iterable )
        {
            if ( formItem instanceof InlineMixin )
            {
                final InlineMixin inline = (InlineMixin) formItem;
                final Mixin mixin = getByName( inline.getMixinName() );
                if ( mixin != null )
                {
                    for ( final FormItem mixinFormItem : mixin.getForm() )
                    {
                        formItems.add( mixinFormItem.copy() );
                    }
                }
                else
                {
                    throw new RuntimeException( "Mixin [" + inline.getMixinName() + "] not found" );
                }
            }
            else if ( formItem instanceof FormItemSet )
            {
                final FormItemSet.Builder formItemSetBuilder = FormItemSet.create( (FormItemSet) formItem );
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transformFormItems( (FormItemSet) formItem ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else if ( formItem instanceof FieldSet )
            {
                final FieldSet.Builder formItemSetBuilder = FieldSet.create( (FieldSet) formItem );
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transformFormItems( (FieldSet) formItem ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else
            {
                formItems.add( formItem.copy() );
            }
        }
        return formItems;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
