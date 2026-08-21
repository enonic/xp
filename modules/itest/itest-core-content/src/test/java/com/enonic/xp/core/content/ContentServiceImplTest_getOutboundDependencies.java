package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_getOutboundDependencies
    extends AbstractContentServiceTest
{
    @Test
    void testThatOutboundDependenciesContainsVariant()
    {
        Content originalContent = this.contentService.create( CreateContentParams.create()
                                                                  .contentData( new PropertyTree() )
                                                                  .displayName( "My Content" )
                                                                  .parent( ContentPath.ROOT )
                                                                  .type( ContentTypeName.folder() )
                                                                  .permissions( AccessControlList.create().build() )
                                                                  .build() );

        DuplicateContentsResult result = contentService.duplicate( DuplicateContentParams.create()
                                                                       .contentId( originalContent.getId() )
                                                                       .includeChildren( false )
                                                                       .variant( true )
                                                                       .name( "Variant Name" )
                                                                       .parent( originalContent.getPath() )
                                                                       .build() );

        final ContentId variantContentId = result.getDuplicatedContents().first();

        // Try to find dependencies for variant content
        ContentIds outboundDependencies = this.contentService.getOutboundDependencies( variantContentId );

        assertTrue( outboundDependencies.contains( originalContent.getId() ) );
    }

    @Test
    void data_reference()
    {
        final Content target = createFolder( "target" );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( target.getId().toString() ) );

        final Content content = createFolder( "referring", data );

        assertTrue( this.contentService.getOutboundDependencies( content.getId() ).contains( target.getId() ) );
    }

    @Test
    void page_template_reference()
    {
        final Content template = createPageTemplate( createSite() );

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "Has a page" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .page( Page.create()
                                                                           .template( PageTemplateKey.from(
                                                                               template.getId().toString() ) )
                                                                           .build() )
                                                                .build() );

        assertTrue( this.contentService.getOutboundDependencies( content.getId() ).contains( template.getId() ),
                    "the template a page points at is an outbound dependency" );
    }

    @Test
    void page_component_config_reference()
    {
        final Content site = createSite();
        final Content template = createPageTemplate( site );
        final Content target = createFolder( "part-config-target" );

        final PropertyTree partConfig = new PropertyTree();
        partConfig.addReference( "myRef", Reference.from( target.getId().toString() ) );

        // a content customized from a template: the page carries no descriptor of its own, only the template and its components
        final Page page = Page.create()
            .template( PageTemplateKey.from( template.getId().toString() ) )
            .regions( Regions.create()
                          .add( Region.create()
                                    .name( "main" )
                                    .add( PartComponent.create()
                                              .descriptor( DescriptorKey.from( "myapp:mypart" ) )
                                              .config( partConfig )
                                              .build() )
                                    .build() )
                          .build() )
            .build();

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "Customized from a template" )
                                                                .parent( site.getPath() )
                                                                .type( ContentTypeName.folder() )
                                                                .page( page )
                                                                .build() );

        assertTrue( this.contentService.getOutboundDependencies( content.getId() ).contains( target.getId() ),
                    "a reference held in a page component config is an outbound dependency" );
    }

    @Test
    void xdata_reference()
    {
        final Content target = createFolder( "xdata-target" );
        final Content content = createFolder( "has-xdata" );

        final PropertyTree xData = new PropertyTree();
        xData.addReference( "myRef", Reference.from( target.getId().toString() ) );

        // patched in rather than created with: x-data on create has to be offered by a mixin mapping
        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> edit.mixins.setValue(
                                           Mixins.create().add( new Mixin( MixinName.from( "myapp:mymixin" ), xData ) ).build() ) )
                                       .build() );

        assertTrue( this.contentService.getOutboundDependencies( content.getId() ).contains( target.getId() ),
                    "a reference held in x-data is an outbound dependency" );
    }

    @Test
    void processed_reference()
    {
        final Content target = createFolder( "html-link-target" );
        final Content content = createFolder( "has-html-area" );

        // the links pulled out of HTML areas are stored apart from the data holding the markup
        this.contentService.patch( PatchContentParams.create()
                                       .contentId( content.getId() )
                                       .patcher( edit -> edit.processedReferences.setValue(
                                           ContentIds.from( target.getId() ) ) )
                                       .build() );

        assertTrue( this.contentService.getOutboundDependencies( content.getId() ).contains( target.getId() ),
                    "a link pulled out of an HTML area is an outbound dependency" );
    }

    @Test
    void dangling_reference_is_kept()
    {
        final ContentId missing = ContentId.from( "no-such-content" );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( missing.toString() ) );

        final Content content = createFolder( "dangling", data );

        // the publish dialog reports these as broken links, so a reference with nothing behind it has to survive
        assertTrue( this.contentService.getOutboundDependencies( content.getId() ).contains( missing ) );
    }

    @Test
    void self_reference_is_excluded()
    {
        final Content content = createFolder( "self" );

        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", Reference.from( content.getId().toString() ) );

        this.contentService.update( new UpdateContentParams().contentId( content.getId() ).editor( edit -> edit.data = data ) );

        final ContentIds outboundDependencies = this.contentService.getOutboundDependencies( content.getId() );

        assertFalse( outboundDependencies.contains( content.getId() ) );
        assertEquals( 0, outboundDependencies.getSize() );
    }

    @Test
    void no_references()
    {
        final Content content = createFolder( "lonely" );

        assertEquals( 0, this.contentService.getOutboundDependencies( content.getId() ).getSize() );
    }

    @Test
    void unknown_content()
    {
        assertThrows( ContentNotFoundException.class,
                      () -> this.contentService.getOutboundDependencies( ContentId.from( "no-such-content" ) ) );
    }

    private Content createSite()
    {
        return this.contentService.create( CreateContentParams.create()
                                               .contentData( new PropertyTree() )
                                               .displayName( "My site" )
                                               .parent( ContentPath.ROOT )
                                               .type( ContentTypeName.site() )
                                               .build() );
    }

    // a page template only lives under the templates folder a site brings with it
    private Content createPageTemplate( final Content site )
    {
        return this.contentService.create( CreateContentParams.create()
                                               .contentData( new PropertyTree() )
                                               .displayName( "A template" )
                                               .parent( ContentPath.from( site.getPath(), "_templates" ) )
                                               .type( ContentTypeName.pageTemplate() )
                                               .build() );
    }

    private Content createFolder( final String displayName )
    {
        return createFolder( displayName, new PropertyTree() );
    }

    private Content createFolder( final String displayName, final PropertyTree data )
    {
        return this.contentService.create( CreateContentParams.create()
                                               .contentData( data )
                                               .displayName( displayName )
                                               .parent( ContentPath.ROOT )
                                               .type( ContentTypeName.folder() )
                                               .build() );
    }
}
