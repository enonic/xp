package com.enonic.xp.core.impl.content.processor;


import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static com.enonic.xp.form.FormItemType.FORM_ITEM_SET;
import static com.enonic.xp.form.FormItemType.INPUT;

@Component
public final class ReferenceContentProcessor
    implements ContentProcessor
{
    private static final String REFERENCE_PREFIX = "_";

    private static final String REFERENCE_SUFFIX = "_references";

    private Parser<ContentIds> parser;

    private ContentTypeService contentTypeService;

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();
        final PropertyTree data = createContentParams.getData();

        final ContentType contentType =
            contentTypeService.getByName( new GetContentTypeParams().contentTypeName( createContentParams.getType() ) );
        final FormItems formItems = contentType.getForm().getFormItems();

        this.processHtmlAreaInputs( formItems, data.getRoot() );

        return new ProcessCreateResult( CreateContentParams.create( createContentParams ).
            contentData( data ).
            build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final CreateAttachments createAttachments = params.getCreateAttachments();

        final ContentEditor editor = editable ->
        {
            final FormItems formItems = params.getContentType().getForm().getFormItems();

            this.removeReferences( editable.data.getRoot() );

            this.processHtmlAreaInputs( formItems, editable.data.getRoot() );

        };
        return new ProcessUpdateResult( createAttachments, editor );
    }

    @Override
    public boolean supports( final ContentType contentType )
    {
        return true;
    }

    private void processHtmlAreaInputs( final FormItems formItems, final PropertySet data )
    {
        formItems.forEach( formItem ->
                           {
                               if ( formItem.getType() == INPUT )
                               {
                                   final Input input = formItem.toInput();
                                   if ( InputTypeName.HTML_AREA.equals( input.getInputType() ) )
                                   {
                                       final Value value = data.getValue( input.getName() );
                                       if ( value != null && !value.isNull() )
                                       {
                                           final List<com.enonic.xp.util.Reference> refs = parser.parse( value.asString() ).stream().map(
                                               id -> com.enonic.xp.util.Reference.from( id.toString() ) ).collect( Collectors.toList() );

                                           data.addReferences( REFERENCE_PREFIX + input.getName().toString() + REFERENCE_SUFFIX, refs );
                                       }
                                   }
                               }
                               else if ( formItem.getType() == FORM_ITEM_SET )
                               {

                                   final FormItemSet formItemSet = formItem.toFormItemSet();
                                   final PropertySet propertySet = data.getSet( formItemSet.getPath().toString() );

                                   if ( propertySet != null )
                                   {
                                       processHtmlAreaInputs( formItemSet.getFormItems(), propertySet );
                                   }

                               }
                           } );
    }

    private void removeReferences( final PropertySet data )
    {
        List<String> names = Lists.newArrayList();

        data.getProperties().forEach( property ->
                                      {
                                          if ( ValueTypes.REFERENCE.equals( property.getType() ) )
                                          {
                                              final String name = property.getName();
                                              if ( name.startsWith( REFERENCE_PREFIX ) && name.endsWith( REFERENCE_SUFFIX ) )
                                              {
                                                  names.add( name );
                                              }
                                          }
                                          else if ( ValueTypes.PROPERTY_SET.equals( property.getType() ) )
                                          {
                                              removeReferences( property.getSet() );
                                          }
                                      } );

        names.forEach( name -> data.removeProperties( name ) );
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setParser( final Parser<ContentIds> parser )
    {
        this.parser = parser;
    }
}
