package com.enonic.xp.portal.view;

public interface ViewProcessor
{
    public String getName();

    public String process( ViewModel viewModel );
}
