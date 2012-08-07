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

        var page = new AdminLiveEdit.components2.Page();
        var regions = new AdminLiveEdit.components2.Regions();
        var windows = new AdminLiveEdit.components2.Windows();
        var contents = new AdminLiveEdit.components2.Contents();
        var paragraphs = new AdminLiveEdit.components2.Paragraphs();
        var highlighter = new AdminLiveEdit.ui2.Highlighter();
        var selectedComponent = new AdminLiveEdit.ui2.SelectedComponent();

        var infoTip = new AdminLiveEdit.ui2.InfoTip();
        var toolTip = new AdminLiveEdit.ui2.ToolTip();
        var componentMenu = new AdminLiveEdit.ui2.ComponentMenu();


    });

}(window));