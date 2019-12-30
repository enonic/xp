package com.enonic.xp.repo.impl.dump.upgrade.flattenedpage;

import java.util.List;
import java.util.Objects;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageSourceConstants.SRC_PAGE_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_COMPONENTS_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_CONFIG_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_CUSTOMIZED_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_DESCRIPTOR_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TEMPLATE_KEY;


public class FlattenedPageIndexUpgrader
{
    private static final List<PathIndexConfig> BASE_CONFIGS =
        List.of( PathIndexConfig.create().indexConfig( IndexConfig.NONE ).path( PropertyPath.from( TGT_COMPONENTS_KEY ) ).build(),
                 PathIndexConfig.create().indexConfig( IndexConfig.MINIMAL ).path(
                     PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_DESCRIPTOR_KEY ) ) ).build(),
                 PathIndexConfig.create().indexConfig( IndexConfig.MINIMAL ).path(
                     PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_TEMPLATE_KEY ) ) ).build(),
                 PathIndexConfig.create().indexConfig( IndexConfig.MINIMAL ).path(
                     PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_CUSTOMIZED_KEY ) ) ).build() );

    private final DescriptorKey descriptorKey;

    private final List<PropertySet> components;

    private FlattenedPageRegionsIndexUpgrader flattenedPageRegionsIndexUpgrader;

    private PatternIndexConfigDocument.Builder result;

    public FlattenedPageIndexUpgrader( final DescriptorKey descriptorKey, final List<PropertySet> components )
    {
        this.descriptorKey = descriptorKey;
        this.components = components;
        this.flattenedPageRegionsIndexUpgrader = new FlattenedPageRegionsIndexUpgrader( components );
    }

    public PatternIndexConfigDocument upgrade( final PatternIndexConfigDocument sourceIndexConfigDocument )
    {
        result = PatternIndexConfigDocument.create( sourceIndexConfigDocument );

        if ( components.size() > 0 )
        {
            addNewConfigs();
        }
        if ( descriptorKey != null )
        {
            upgradeOldConfigs( sourceIndexConfigDocument );
        }

        if ( components.size() > 0 )
        {
            return removeOldConfigs( this.flattenedPageRegionsIndexUpgrader.upgrade( result.build() ) );
        }

        return removeOldConfigs( result.build() );
    }

    private PatternIndexConfigDocument removeOldConfigs( final PatternIndexConfigDocument sourceIndexConfigDocument )
    {
        result = PatternIndexConfigDocument.create( sourceIndexConfigDocument );

        sourceIndexConfigDocument.getPathIndexConfigs().stream().
            filter( pathIndexConfig -> pathIndexConfig.getPath().toString().startsWith( SRC_PAGE_KEY ) ).
            forEach( result::remove );

        return result.build();
    }

    private void addNewConfigs()
    {
        BASE_CONFIGS.forEach( result::add );

        if ( this.descriptorKey != null )
        {
            result.add( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_CONFIG_KEY, getSanitizedAppName( this.descriptorKey ),
                                     getSanitizedComponentName( descriptorKey ), "*" ), IndexConfig.BY_TYPE );
        }
    }

    private void upgradeOldConfigs( final PatternIndexConfigDocument sourceIndexConfigDocument )
    {
        sourceIndexConfigDocument.getPathIndexConfigs().stream().
            map( this::upgradeConfigPath ).
            filter( Objects::nonNull ).
            forEach( result::add );
    }


    private PathIndexConfig upgradeConfigPath( final PathIndexConfig source )
    {
        final String sourcePath = source.getPath().toString();

        if ( this.descriptorKey != null )
        {
            if ( sourcePath.startsWith( String.join( ELEMENT_DIVIDER, PAGE, TGT_CONFIG_KEY ) ) )
            {
                final String newPathPrefix =
                    String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_CONFIG_KEY, getSanitizedAppName( this.descriptorKey ),
                                 getSanitizedComponentName( this.descriptorKey ) );
                final String newPath = sourcePath.replace( String.join( ELEMENT_DIVIDER, PAGE, TGT_CONFIG_KEY ), newPathPrefix );

                return PathIndexConfig.create().indexConfig( source.getIndexConfig() ).path( PropertyPath.from( newPath ) ).build();
            }
        }

        return null;
    }

    private String getSanitizedAppName( final DescriptorKey descriptor )
    {
        return descriptor.getApplicationKey().toString().replace( ".", "-" );
    }

    private String getSanitizedComponentName( final DescriptorKey descriptor )
    {
        return descriptor.getName().replace( ".", "-" );
    }
}