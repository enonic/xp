(function (window) {

    // Map jQuery served with Live Edit
    window.$liveedit = $.noConflict(true);

    // Namespaces
    if (!window.AdminLiveEdit) {
        window.AdminLiveEdit = {};
        window.AdminLiveEdit.components = {};
        window.AdminLiveEdit.ui = {};
        window.AdminLiveEdit.ui2 = {};
    }

    $liveedit(document).ready(function () {
        var scripts = [
            'Util',
            'PubSub',
            'components2.Base',
            'components2.Page',
            'components2.Regions',
            'components2.Windows',
            'components2.Contents',
            'components2.Paragraphs',
            'ui2.Base',
            'ui2.Button',
            'ui2.ComponentMenu',
            'ui2.Highlighter',
            'ui2.SelectedComponent',
            'ui2.ToolTip',
            'ui2.InfoTip',
            'ui.DragDrop',
            'PageLeave'
        ];


        function loadScripts(index) {
            var fileName = scripts[index].replace(/\./g, '/');
            console.log('Load: ' + fileName);
            $liveedit.getScript('../live-edit/js/' + fileName + '.js', function () {
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