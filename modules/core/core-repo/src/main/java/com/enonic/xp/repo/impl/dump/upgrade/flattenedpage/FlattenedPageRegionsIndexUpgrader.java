package com.enonic.xp.repo.impl.dump.upgrade.flattenedpage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.page.DescriptorKey;

import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_COMPONENTS_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_CONFIG_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_DESCRIPTOR_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_ID_KEY;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TYPE_VALUE.FRAGMENT;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TYPE_VALUE.IMAGE;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TYPE_VALUE.LAYOUT;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TYPE_VALUE.PART;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_TYPE_VALUE.TEXT;
import static com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageTargetConstants.TGT_VALUE_KEY;


public class FlattenedPageRegionsIndexUpgrader
{
    private static final List<PathIndexConfig> BASE_REGIONS_CONFIGS = List.of( PathIndexConfig.create().path(
        PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, IMAGE, TGT_ID_KEY ) ) ).indexConfig(
        IndexConfig.MINIMAL ).build(), PathIndexConfig.create().path(
        PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, FRAGMENT, TGT_ID_KEY ) ) ).indexConfig(
        IndexConfig.MINIMAL ).build(), PathIndexConfig.create().path(
        PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, PART, TGT_DESCRIPTOR_KEY ) ) ).indexConfig(
        IndexConfig.MINIMAL ).build(), PathIndexConfig.create().path(
        PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, LAYOUT, TGT_DESCRIPTOR_KEY ) ) ).indexConfig(
        IndexConfig.MINIMAL ).build(), PathIndexConfig.create().path(
        PropertyPath.from( String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, TEXT, TGT_VALUE_KEY ) ) ).indexConfig(
        IndexConfig.create( IndexConfig.FULLTEXT ).
            addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
            build() ).build() );

    private static final Pattern HTML_AREA_CONFIG_PATH_PATTERN =
        Pattern.compile( "(?i)^page\\.(region\\.component\\.(layoutcomponent|partcomponent)\\.)+config\\.(\\S+)$" );

    private final List<PropertySet> components;

    private PatternIndexConfigDocument.Builder result;


    FlattenedPageRegionsIndexUpgrader( final List<PropertySet> components )
    {
        this.components = components;
    }

    public PatternIndexConfigDocument upgrade( final PatternIndexConfigDocument sourceIndexConfigDocument )
    {
        result = PatternIndexConfigDocument.create( sourceIndexConfigDocument );

        addNewConfigs();
        upgradeOldConfigs( sourceIndexConfigDocument );

        return result.build();
    }

    private void addNewConfigs()
    {
        BASE_REGIONS_CONFIGS.forEach( result::add );
    }

    private void upgradeOldConfigs( final PatternIndexConfigDocument sourceIndexConfigDocument )
    {
        this.upgradeComponents();

        sourceIndexConfigDocument.getPathIndexConfigs().
            forEach( this::upgradeHtmlAreas );
    }


    private void upgradeComponents()
    {
        this.components.forEach( propertySet -> {
            PropertySet componentSet;

            componentSet = propertySet.getSet( LAYOUT );
            if ( componentSet != null )
            {
                upgradeDescriptorBasedComponent( componentSet, LAYOUT );
            }

            componentSet = propertySet.getSet( PART );
            if ( componentSet != null )
            {
                upgradeDescriptorBasedComponent( componentSet, PART );
            }

        } );
    }

    private void upgradeDescriptorBasedComponent( PropertySet componentSet, final String componentType )
    {
        final String descriptorKeyStr = componentSet.getString( TGT_DESCRIPTOR_KEY );

        if ( descriptorKeyStr != null )
        {
            final DescriptorKey descriptorKey = DescriptorKey.from( descriptorKeyStr );

            result.add(
                String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY, componentType, TGT_CONFIG_KEY, getSanitizedAppName( descriptorKey ),
                             getSanitizedComponentName( descriptorKey ), "*" ), IndexConfig.BY_TYPE );
        }
    }

    private void upgradeHtmlAreas( final PathIndexConfig pathIndexConfig )
    {
        final Matcher matcher = HTML_AREA_CONFIG_PATH_PATTERN.matcher( pathIndexConfig.getPath().toString() );

        if ( matcher.find() )
        {
            this.components.forEach( componentSet -> {

                final String componentNewType = FlattenedPageDataUpgrader.getTargetType( matcher.group( 2 ) );

                if ( componentNewType != null )
                {
                    final String descriptorKeyStr =
                        componentSet.getString( String.join( ELEMENT_DIVIDER, componentNewType, TGT_DESCRIPTOR_KEY ) );

                    if ( descriptorKeyStr != null )
                    {
                        final DescriptorKey descriptorKey = DescriptorKey.from( descriptorKeyStr );

                        final String property = componentSet.getString(
                            String.join( ELEMENT_DIVIDER, componentNewType, TGT_CONFIG_KEY, getSanitizedAppName( descriptorKey ),
                                         getSanitizedComponentName( descriptorKey ), matcher.group( 3 ) ) );

                        if ( property != null )
                        {

                            String newHtmlAreaPath = String.join( ELEMENT_DIVIDER, TGT_COMPONENTS_KEY,
                                                                  FlattenedPageDataUpgrader.getTargetType( matcher.group( 2 ) ),
                                                                  TGT_CONFIG_KEY, getSanitizedAppName( descriptorKey ),
                                                                  getSanitizedComponentName( descriptorKey ), matcher.group( 3 ) );

                            result.add( newHtmlAreaPath, pathIndexConfig.getIndexConfig() );
                        }
                    }
                }
            } );
        }

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
