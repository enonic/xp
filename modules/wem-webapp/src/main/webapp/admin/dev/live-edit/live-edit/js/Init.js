(function (window) {

    $liveedit(document).ready(function () {

        var scripts = [
            'Util',
            'Windows',
            'Regions',
            'Highlighter',
            'Tooltip',
            'Button',
            'ContextMenu',
            'DragDrop',
            'PubSub',
            'PageLeave'
            //,'Log'
        ];

        function loadScript(index) {
            $liveedit.getScript('../live-edit/js/' + scripts[index] + '.js', function () {
                index++;
                if (index < scripts.length) {
                    loadScript(index);
                } else {
                    // Finish loading all scripts, execute.
                    var j = 0;

                    for (j = 0; j < scripts.length; j++) {
                        if (AdminLiveEdit[scripts[j]] && AdminLiveEdit[scripts[j]].init) {
                            AdminLiveEdit[scripts[j]].init();
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

        loadScript(0);
    });

}(window));