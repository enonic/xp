(function ($) {
    'use strict';

    $(window).load(function () {
        // Timeout for 18/4
        setTimeout(function () {
            $('.live-edit-loader-splash-container').fadeOut('fast', function () {
                $(this).remove();
                var page                = new AdminLiveEdit.model.component.Page();
                var regions             = new AdminLiveEdit.model.component.Region();
                var layout              = new AdminLiveEdit.model.component.Layout();
                var parts               = new AdminLiveEdit.model.component.Part();
                var contents            = new AdminLiveEdit.model.component.Content();
                var paragraphs          = new AdminLiveEdit.model.component.Paragraph();

                var htmlElementReplacer = new AdminLiveEdit.view.HtmlElementReplacer();
                var outliner            = new AdminLiveEdit.view.Highlighter();
                var toolTip             = new AdminLiveEdit.view.ToolTip();
                var cursor              = new AdminLiveEdit.view.Cursor();
                var menu                = new AdminLiveEdit.view.menu.Menu();
                var shader              = new AdminLiveEdit.view.Shader();
                var htmleditor          = new AdminLiveEdit.view.htmleditor.Editor();
                var componentBar        = new AdminLiveEdit.view.componentbar.ComponentBar();
                var mutationObserver    = new AdminLiveEdit.MutationObserver();

                AdminLiveEdit.DragDropSort.initialize();

                $(window).resize(function () {
                    $(window).trigger('liveEdit.onWindowResize');
                });
            });
        }, 300);


    });

    $(document).ready(function () {

        // *******************************************************************************************************************************//
        // Experiment: Simple replace all A href's on page in order to not navigate if a link is clicked.
        //$('a').attr('href', '#');
        // *******************************************************************************************************************************//
        // Experiment: Move all scripts without @src to the body element in order to prevent script elements to be dragged.
        // TODO: Update CSS selector to only include page components.
        /*
        var $scripts = $('script:not([src])');
        $scripts.each(function () {
            var script = this;
            var $body = $('body')[0];
            // Use standard DOM appendChild as jQuery append is buggy regarding script elements.
            $body.appendChild(script);
        });
        */

    });

}($liveedit));