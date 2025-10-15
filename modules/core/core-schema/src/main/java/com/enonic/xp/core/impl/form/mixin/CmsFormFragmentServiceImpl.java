package com.enonic.xp.core.impl.form.mixin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.FormFragment;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.content.CmsFormFragmentService;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptors;

@Component(immediate = true)
public final class CmsFormFragmentServiceImpl
    implements CmsFormFragmentService
{
    private static final Logger LOG = LoggerFactory.getLogger( CmsFormFragmentServiceImpl.class );

    private final CmsFormFragmentLoader mixinLoader;

    private final ApplicationService applicationService;

    @Activate
    public CmsFormFragmentServiceImpl( @Reference final ApplicationService applicationService, @Reference final ResourceService resourceService )
    {
        this.mixinLoader = new CmsFormFragmentLoader( resourceService );
        this.applicationService = applicationService;
    }

    @Override
    public FormFragmentDescriptor getByName( final FormFragmentName name )
    {
        return mixinLoader.get( name );
    }

    @Override
    public FormFragmentDescriptors getAll()
    {
        final FormFragmentDescriptors.Builder builder = FormFragmentDescriptors.create();

        for ( final Application application : this.applicationService.getInstalledApplications() )
        {
            builder.addAll( getByApplication( application.getKey() ) );
        }

        return builder.build();
    }

    @Override
    public FormFragmentDescriptors getByApplication( final ApplicationKey key )
    {
        return mixinLoader.findNames( key ).stream().map( this::getByName ).filter( Objects::nonNull ).collect( FormFragmentDescriptors.collector() );
    }

    @Override
    public Form inlineFormItems( final Form form )
    {
        return doInlineFormItems( form, new HashSet<>() );
    }

    private Form doInlineFormItems( final Form form, final Set<FormFragmentName> inlineMixins )
    {
        final Form.Builder transformedForm = Form.create();
        final List<FormItem> transformedFormItems = transformFormItems( form, inlineMixins );
        transformedFormItems.forEach( transformedForm::addFormItem );
        return transformedForm.build();
    }

    private List<FormItem> transformFormItems( final Iterable<FormItem> iterable, final Set<FormFragmentName> inlineMixinStack )
    {
        final List<FormItem> formItems = new ArrayList<>();
        for ( final FormItem formItem : iterable )
        {
            if ( formItem instanceof FormFragment )
            {
                final FormFragment inline = (FormFragment) formItem;
                final FormFragmentName formFragmentName = inline.getFormFragmentName();
                final FormFragmentDescriptor mixin = getByName( formFragmentName );
                if ( mixin != null )
                {
                    if ( inlineMixinStack.contains( formFragmentName ) )
                    {
                        final String error =
                            "Cycle detected in mixin [" + mixin.getName() + "]. It contains an inline mixin that references itself.";
                        LOG.error( error );
                        throw new IllegalArgumentException( error );
                    }

                    inlineMixinStack.add( formFragmentName );
                    final Form mixinForm = doInlineFormItems( mixin.getForm(), inlineMixinStack );
                    inlineMixinStack.remove( formFragmentName );

                    for ( final FormItem mixinFormItem : mixinForm )
                    {
                        formItems.add( mixinFormItem.copy() );
                    }
                }
                else
                {
                    throw new IllegalArgumentException( "Inline mixin [" + formFragmentName + "] not found" );
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
                    optionBuilder.addFormItems( transformFormItems( option, inlineMixinStack ) );
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
}
