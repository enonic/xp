package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.RenameContentParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_rename
    extends AbstractContentServiceTest
{
    @Test
    public void rename()
        throws Exception
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
    public void renameToUnnamed()
        throws Exception
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


    private ExtraDatas createExtraDatas()
    {
        return ExtraDatas.create().add( new ExtraData( XDataName.from( "com.enonic.app.test:mixin" ), new PropertyTree() ) ).build();
    }

    private PropertySet createSiteConfig( PropertyTree tree )
    {
        PropertySet set = tree.newSet();
        set.addString( "applicationKey", "com.enonic.app.test" );
        set.addSet( "config" );
        return set;
    }
}
