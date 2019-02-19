package com.enonic.xp.repo.impl.dump.upgrade.flattenedpage;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

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
    private static final List<PathIndexConfig> BASE_CONFIGS = Lists.newArrayList(
        PathIndexConfig.create().indexConfig( IndexConfig.NONE ).path( PropertyPath.from( TGT_COMPONENTS_KEY ) ).build(),
        PathIndexConfig.create().indexConfig( IndexConfig.MINIMAL ).path(
            PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_DESCRIPTOR_KEY ) ) ).build(),
        PathIndexConfig.create().indexConfig( IndexConfig.MINIMAL ).path(
            PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_TEMPLATE_KEY ) ) ).build(),
        PathIndexConfig.create().indexConfig( IndexConfig.MINIMAL ).path(
            PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_CUSTOMIZED_KEY ) ) ).build() );

    private final DescriptorKey descriptorKey;

    private FlattenedPageRegionsIndexUpgrader flattenedPageRegionsIndexUpgrader;

    private PatternIndexConfigDocument.Builder result;

    public FlattenedPageIndexUpgrader( final DescriptorKey descriptorKey, final List<PropertySet> components )
    {
        this.descriptorKey = descriptorKey;
        this.flattenedPageRegionsIndexUpgrader = new FlattenedPageRegionsIndexUpgrader( components );
    }

    public PatternIndexConfigDocument upgrade( final PatternIndexConfigDocument sourceIndexConfigDocument )
    {
        result = PatternIndexConfigDocument.create( sourceIndexConfigDocument );

        addNewConfigs();
        upgradeOldConfigs( sourceIndexConfigDocument );

        if ( this.flattenedPageRegionsIndexUpgrader.needAnUpgrade( sourceIndexConfigDocument ) )
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

    public boolean needAnUpgrade( final PatternIndexConfigDocument sourceIndexConfigDocument )
    {
        return sourceIndexConfigDocument.getPathIndexConfigs().stream().
            anyMatch( pathIndexConfig -> pathIndexConfig.matches( SRC_PAGE_KEY ) );
    }

    private void addNewConfigs()
    {
        BASE_CONFIGS.forEach( result::add );

        if ( this.descriptorKey != null )
        {
            result.add( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_CONFIG_KEY, getAppName( this.descriptorKey ), "*" ),
                        IndexConfig.BY_TYPE );
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
                final String newPathPrefix = String.format( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PAGE, TGT_CONFIG_KEY, "%s" ),
                                                            getAppName( this.descriptorKey ) );
                final String newPath = sourcePath.replace( String.join( ELEMENT_DIVIDER, PAGE, TGT_CONFIG_KEY ), newPathPrefix );

                return PathIndexConfig.create().indexConfig( source.getIndexConfig() ).path( PropertyPath.from( newPath ) ).build();
            }
        }

        return null;
    }

    private String getAppName( final DescriptorKey descriptor )
    {
        return descriptor.getApplicationKey().toString().replace( ".", "-" );
    }
}