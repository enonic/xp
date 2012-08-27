(function () {
    'use strict';

    $liveedit(window).load(function () {
        $liveedit('.live-edit-loader-splash-container').remove();
        var page = new AdminLiveEdit.components.Page();
        var regions = new AdminLiveEdit.components.Regions();
        var windows = new AdminLiveEdit.components.Windows();
        var contents = new AdminLiveEdit.components.Contents();
        var paragraphs = new AdminLiveEdit.components.Paragraphs();
        var highlighter = new AdminLiveEdit.view.Highlighter();
        var componentSelector = new AdminLiveEdit.view.ComponentSelector();
        var infoTip = new AdminLiveEdit.view.InfoTip();
        var toolTip = new AdminLiveEdit.view.ToolTip();
        var cursor = new AdminLiveEdit.view.Cursor();
        var componentMenu = new AdminLiveEdit.view.componentmenu.ComponentMenu();
        var shader = new AdminLiveEdit.view.Shader();
        var htmlElementReplacer = new AdminLiveEdit.HtmlElementReplacer();

        AdminLiveEdit.DragDrop.init();
    });

    $liveedit(document).ready(function () {

        // *******************************************************************************************************************************//
        // Experiment: Simple replace all A href's on page in order to not navigate if a link is clicked.
        $liveedit('a').attr('href', '#');
        // *******************************************************************************************************************************//
        // Experiment: Move all scripts without @src to the body element in order to prevent script elements to be dragged.
        // TODO: Update CSS selector to only include page components.
        var $scripts = $liveedit('script:not([src])');
        $scripts.each(function () {
            var script = this;
            var $body = $liveedit('body')[0];
            // Use standard DOM appendChild as jQuery append is buggy regarding script elements.
            $body.appendChild(script);
        });

    });

}());