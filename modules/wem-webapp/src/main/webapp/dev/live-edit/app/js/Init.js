(function () {
    'use strict';

    $liveedit(window).load(function () {
        $liveedit('.live-edit-loader-splash-container').remove();
        var selection           = new AdminLiveEdit.Selection();
        var htmlElementReplacer = new AdminLiveEdit.HtmlElementReplacer();
        var page                = new AdminLiveEdit.model.component.Page();
        var regions             = new AdminLiveEdit.model.component.Regions();
        var windows             = new AdminLiveEdit.model.component.Windows();
        var contents            = new AdminLiveEdit.model.component.Contents();
        var paragraphs          = new AdminLiveEdit.model.component.Paragraphs();
        var highlighter         = new AdminLiveEdit.view.Highlighter();
        var componentTip        = new AdminLiveEdit.view.ComponentTip();
        var toolTip             = new AdminLiveEdit.view.ToolTip();
        var cursor              = new AdminLiveEdit.view.Cursor();
        var componentMenu       = new AdminLiveEdit.view.componentmenu.ComponentMenu();
        var shader              = new AdminLiveEdit.view.Shader();

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