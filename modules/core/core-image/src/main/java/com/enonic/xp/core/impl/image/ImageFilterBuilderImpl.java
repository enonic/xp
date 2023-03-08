/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.enonic.xp.core.impl.image.effect.ImageFilters;
import com.enonic.xp.core.impl.image.parser.FilterExpr;
import com.enonic.xp.core.impl.image.parser.FilterSetExpr;

@Component(immediate = true, configurationPid = "com.enonic.xp.image")
public final class ImageFilterBuilderImpl
    implements ImageFilterBuilder
{
    private volatile int maxTotalNumberOfFilters;

    private volatile Map<String, FilterCommand> imageFilterRegistry;

    @Activate
    @Modified
    public void activate( final ImageConfig config )
    {
        maxTotalNumberOfFilters = config.filters_maxTotal();
        final ImageFilters imageFilters = new ImageFilters();

        Map<String, FilterCommand> map = new HashMap<>();
        map.put( "block", imageFilters::block );
        map.put( "blur", imageFilters::blur );
        map.put( "border", imageFilters::border );
        map.put( "bump", imageFilters::bump );
        map.put( "colorize", imageFilters::colorize );
        map.put( "edge", imageFilters::edge );
        map.put( "emboss", imageFilters::emboss );
        map.put( "fliph", imageFilters::fliph );
        map.put( "flipv", imageFilters::flipv );
        map.put( "rotate90", imageFilters::rotate90 );
        map.put( "rotate180", imageFilters::rotate180 );
        map.put( "rotate270", imageFilters::rotate270 );
        map.put( "gamma", imageFilters::gamma );
        map.put( "grayscale", imageFilters::grayscale );
        map.put( "hsbadjust", imageFilters::hsbadjust );
        map.put( "hsbcolorize", imageFilters::hsbcolorize );
        map.put( "invert", imageFilters::invert );
        map.put( "rgbadjust", imageFilters::rgbadjust );
        map.put( "rounded", imageFilters::rounded );
        map.put( "sepia", imageFilters::sepia );
        map.put( "sharpen", imageFilters::sharpen );
        imageFilterRegistry = map;
    }

    @Override
    public ImageFunction build( final FilterSetExpr set )
    {
        final List<FilterExpr> list = set.getList();
        if ( list.size() > maxTotalNumberOfFilters )
        {
            throw new IllegalArgumentException( "Max number of filters exceeded " + list.size() );
        }

        return new ImageFunctionChain( list.stream().map( this::build ).collect( Collectors.toUnmodifiableList() ) );
    }

    private ImageFunction build( final FilterExpr expr )
    {
        final FilterCommand filterCommand = this.imageFilterRegistry.get( expr.getName() );
        if ( filterCommand == null )
        {
            throw new IllegalArgumentException( "Unknown filter " + expr.getName() );
        }

        return filterCommand.build( expr.getArguments() );
    }
}
