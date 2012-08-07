(function (window) {

    // Map jQuery served with Live Edit
    window.$liveedit = $.noConflict(true);

    // Root Namespace
    if (!window.AdminLiveEdit) {
        window.AdminLiveEdit = {};
    }

    $liveedit(document).ready(function () {

        // *******************************************************************************************************************************//
        // Experiment: Simple replace all A href's on page in order to not navigate if a link is clicked.
        $liveedit('a').attr('href', '#');
        // *******************************************************************************************************************************//
        // Experiment: Move all scripts without @src to the body element in order to prevent script elements to be dragged.
        // TODO: Update CSS selector to only include page components.
        var $scripts = $liveedit('script:not([src])');
        $scripts.each(function (i) {
            var script = this;
            var $body = $liveedit('body')[0];
            // Use standard DOM appendChild as jQuery append is buggy regarding script elements.
            $body.appendChild(script);
        });
        // *******************************************************************************************************************************//

        // TODO: Remove timeout when loader splash(CMS-29) is accepted.
        setTimeout(function() {
            var page = new AdminLiveEdit.page.components.Page();
            var regions = new AdminLiveEdit.page.components.Regions();
            var windows = new AdminLiveEdit.page.components.Windows();
            var contents = new AdminLiveEdit.page.components.Contents();
            var paragraphs = new AdminLiveEdit.page.components.Paragraphs();
            var highlighter = new AdminLiveEdit.ui2.Highlighter();
            var selectedComponent = new AdminLiveEdit.ui2.SelectedComponent();
            var infoTip = new AdminLiveEdit.ui2.InfoTip();
            var toolTip = new AdminLiveEdit.ui2.ToolTip();
            var componentMenu = new AdminLiveEdit.ui2.ComponentMenu();

            AdminLiveEdit.ui2.DragDrop.init();

            $liveedit('.live-edit-loader-splash-container').remove();

        }, 1000);

    });

}(window));