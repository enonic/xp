package com.enonic.xp.core.impl.schema.mixin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.InlineMixin;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;

@Component(immediate = true)
public final class MixinServiceImpl
    implements MixinService
{
    private final static Logger LOG = LoggerFactory.getLogger( MixinServiceImpl.class );

    private ApplicationService applicationService;

    private ResourceService resourceService;

    public MixinServiceImpl()
    {
    }

    @Override
    public Mixin getByName( final MixinName name )
    {
        return new MixinLoader( this.resourceService ).get( name );
    }

    @Override
    public Mixins getByNames( final MixinNames names )
    {
        if ( names == null )
        {
            return Mixins.empty();
        }

        return Mixins.from( names.stream().map( this::getByName ).filter( Objects::nonNull ).collect( Collectors.toList() ) );
    }

    @Override
    public Mixins getAll()
    {
        final Set<Mixin> list = Sets.newLinkedHashSet();

        for ( final Application application : this.applicationService.getInstalledApplications() )
        {
            final Mixins types = getByApplication( application.getKey() );
            list.addAll( types.getList() );
        }

        return Mixins.from( list );
    }

    @Override
    public Mixins getByApplication( final ApplicationKey key )
    {
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
    public Form inlineFormItems( final Form form )
    {
        return doInlineFormItems( form, new HashSet<>() );
    }

    private Form doInlineFormItems( final Form form, final Set<MixinName> inlineMixins )
    {
        final Form.Builder transformedForm = Form.create();
        final List<FormItem> transformedFormItems = transformFormItems( form, inlineMixins );
        transformedFormItems.forEach( transformedForm::addFormItem );
        return transformedForm.build();
    }

    private List<FormItem> transformFormItems( final Iterable<FormItem> iterable, final Set<MixinName> inlineMixinStack )
    {
        final List<FormItem> formItems = new ArrayList<>();
        for ( final FormItem formItem : iterable )
        {
            if ( formItem instanceof InlineMixin )
            {
                final InlineMixin inline = (InlineMixin) formItem;
                final MixinName mixinName = inline.getMixinName();
                final Mixin mixin = getByName( mixinName );
                if ( mixin != null )
                {
                    if ( inlineMixinStack.contains( mixinName ) )
                    {
                        final String error =
                            "Cycle detected in mixin [" + mixin.getName() + "]. It contains an inline mixin that references itself.";
                        LOG.error( error );
                        throw new IllegalArgumentException( error );
                    }

                    inlineMixinStack.add( mixinName );
                    final Form mixinForm = doInlineFormItems( mixin.getForm(), inlineMixinStack );
                    inlineMixinStack.remove( mixinName );

                    for ( final FormItem mixinFormItem : mixinForm )
                    {
                        formItems.add( mixinFormItem.copy() );
                    }
                }
                else
                {
                    throw new IllegalArgumentException( "Inline mixin [" + mixinName + "] not found" );
                }
            }
            else if ( formItem instanceof FormItemSet )
            {
                final FormItemSet.Builder formItemSetBuilder = FormItemSet.create( (FormItemSet) formItem );
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transformFormItems( (FormItemSet) formItem, inlineMixinStack ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else if ( formItem instanceof FieldSet )
            {
                final FieldSet.Builder formItemSetBuilder = FieldSet.create( (FieldSet) formItem );
                formItemSetBuilder.clearFormItems();
                formItemSetBuilder.addFormItems( transformFormItems( (FieldSet) formItem, inlineMixinStack ) );
                formItems.add( formItemSetBuilder.build() );
            }
            else if ( formItem instanceof FormOptionSet )
            {
                final FormOptionSet.Builder formOptionSetBuilder = FormOptionSet.create( (FormOptionSet) formItem );
                formOptionSetBuilder.clearOptions();
                for ( FormOptionSetOption option : (FormOptionSet) formItem )
                {
                    final FormOptionSetOption.Builder optionBuilder = FormOptionSetOption.create( option );
                    optionBuilder.clearFormItems();
                    optionBuilder.addFormItems( transformFormItems( option.getFormItems(), inlineMixinStack ) );
                    formOptionSetBuilder.addOptionSetOption( optionBuilder.build() );
                }
                formItems.add( formOptionSetBuilder.build() );
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
