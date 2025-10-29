package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Mixin;
import com.enonic.xp.content.Mixins;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.MixinMapping;
import com.enonic.xp.site.MixinMappings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class ContentServiceImplTest_rename
    extends AbstractContentServiceTest
{
    @Test
    void rename()
    {
        final PropertyTree siteData = new PropertyTree();
        siteData.setSet( "siteConfig", this.createSiteConfig( siteData ) );
        final Content site = createContent( ContentPath.ROOT, "site", siteData, ContentTypeName.site() );

        final Content content = createContent( site.getPath(), "child", new PropertyTree(), this.createExtraDatas() );

        final RenameContentParams params =
            RenameContentParams.create().contentId( content.getId() ).newName( ContentName.from( "newName" ) ).build();

        final Content result = this.contentService.rename( params );

        assertEquals( "newName", result.getName().toString() );
        assertTrue( result.isValid() );
        assertFalse( result.getValidationErrors().hasErrors() );
    }

    @Test
    void renameToUnnamed()
    {
        final PropertyTree siteData = new PropertyTree();
        siteData.setSet( "siteConfig", this.createSiteConfig( siteData ) );
        final Content site = createContent( ContentPath.ROOT, "site", siteData, ContentTypeName.site() );

        final Content content = createContent( site.getPath(), "child", new PropertyTree(), this.createExtraDatas() );

        final RenameContentParams params =
            RenameContentParams.create().contentId( content.getId() ).newName( ContentName.from( "__unnamed__" ) ).build();

        final Content result = this.contentService.rename( params );

        assertTrue( result.getName().isUnnamed() );
        assertFalse( result.isValid() );
        assertTrue( result.getValidationErrors().hasErrors() );
    }


    private Mixins createExtraDatas()
    {
        final MixinName mixinName = MixinName.from( "com.enonic.app.test:xdata1" );

        when( resourceService.processResource( isA( ResourceProcessor.class ) ) ).thenReturn( CmsDescriptor.create()
                                                                                                  .applicationKey( ApplicationKey.from(
                                                                                                      "com.enonic.app.test" ) )
                                                                                                  .mixinMappings( MixinMappings.from(
                                                                                                      MixinMapping.create()
                                                                                                          .mixinName( mixinName )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( false )
                                                                                                          .build() ) )
                                                                                                  .build() );

        final MixinDescriptor mixinDescriptor = MixinDescriptor.create().name( mixinName ).form( Form.create().build() ).build();
        when( mixinService.getByName( mixinDescriptor.getName() ) ).thenReturn( mixinDescriptor );

        return Mixins.create().add( new Mixin( mixinName, new PropertyTree() ) ).build();
    }

    private PropertySet createSiteConfig( PropertyTree tree )
    {
        PropertySet set = tree.newSet();
        set.addString( "applicationKey", "com.enonic.app.test" );
        set.addSet( "config" );
        return set;
    }
}
