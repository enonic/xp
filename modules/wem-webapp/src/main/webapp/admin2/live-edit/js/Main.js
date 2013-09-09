((function ($) {
    'use strict';

    $(window).load(function () {
        var loaderSplash = $('.live-edit-loader-splash-container');
        loaderSplash.fadeOut('fast', function () {
            loaderSplash.remove();

            new LiveEdit.component.mouseevent.Page();
            new LiveEdit.component.mouseevent.Region();
            new LiveEdit.component.mouseevent.Layout();
            new LiveEdit.component.mouseevent.Part();
            new LiveEdit.component.mouseevent.Image();
            new LiveEdit.component.mouseevent.Paragraph();
            new LiveEdit.component.mouseevent.Content();

            new LiveEdit.component.ComponentResizeObserver();

            new LiveEdit.ui.Highlighter();
            new LiveEdit.ui.ToolTip();
            new LiveEdit.ui.Cursor();
            new LiveEdit.ui.contextmenu.ContextMenu();
            new LiveEdit.ui.Shader();
            new LiveEdit.ui.Editor();

            LiveEdit.DragDropSort.init();

            $(window).resize(function () {
                return $(window).trigger('resizeBrowserWindow.liveEdit');
            });

            $(window).unload(function () {
                return console.log('Clean up any css classes etc. that live edit / sortable has added');
            });

            console.log('Live Edit Initialized. Using jQuery version: ' + $.fn.jquery);
        });
    });

    $(document).ready(function () {
        $(document).on('mousedown', 'btn, button, a, select, input', function (event) {
            event.preventDefault();
            return false;
        });
    });
})($liveEdit));
//# sourceMappingURL=Main.js.map
