(function (window) {

    // Map jQuery served with Live Edit
    window.$liveedit = $.noConflict(true);

    // Namespaces
    if (!window.AdminLiveEdit) {
        window.AdminLiveEdit = {};
        window.AdminLiveEdit.components = {};
        window.AdminLiveEdit.ui = {};
    }

    $liveedit(document).ready(function () {
        var scripts = [
            'Util',
            'PubSub',
            'components.PageComponent',
            'components.WindowComponents',
            'components.RegionComponents',
            'components.ParagraphComponents',
            'components.ContentComponents',
            'ui.Highlighter',
            'ui.InfoTip',
            'ui.ToolTip',
            'ui.Button',
            'ui.ContextMenu',
            'ui.DragDrop',
            'PageLeave'
        ];


        function loadScripts(index) {
            console.log('Load: ' + scripts[index].replace(/\./g, '/'));
            $liveedit.getScript('../live-edit/js/' + scripts[index].replace(/\./g, '/') + '.js', function () {
                index++;
                if (index < scripts.length) {
                    loadScripts(index);
                } else {
                    // Finish loading all scripts, execute.
                    var j = 0,
                        script;
                    for (j = 0; j < scripts.length; j++) {
                        script = eval('AdminLiveEdit.' + scripts[j]);
                        if (script && script.init) {
                            script.init();
                        }
                    }
                }
            });
        }





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

        loadScripts(0);
    });

}(window));