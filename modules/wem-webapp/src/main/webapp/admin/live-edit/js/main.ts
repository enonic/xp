declare var Ext;
declare var Admin;
declare var CONFIG;

declare var $liveEdit;

(function ($) {
    'use strict';

    $(window).load(() => {
        // Fade out the loader splash and start the app.
        var loaderSplash:JQuery = $('.live-edit-loader-splash-container');
        loaderSplash.fadeOut('fast', () => {
            loaderSplash.remove();

            new LiveEdit.component.mouseevent.Page();
            new LiveEdit.component.mouseevent.Region();
            new LiveEdit.component.mouseevent.Layout();
            new LiveEdit.component.mouseevent.Part();
            new LiveEdit.component.mouseevent.Image();
            new LiveEdit.component.mouseevent.Paragraph();
            new LiveEdit.component.mouseevent.Content();

            new LiveEdit.component.helper.ComponentResizeObserver();

            new LiveEdit.ui.Highlighter();
            new LiveEdit.ui.ToolTip();
            new LiveEdit.ui.Cursor();
            new LiveEdit.ui.contextmenu.ContextMenu();
            new LiveEdit.ui.Shader();
            new LiveEdit.ui.Editor();

            LiveEdit.component.dragdropsort.DragDropSort.init();

            $(window).resize(() => $(window).trigger('resizeBrowserWindow.liveEdit'));

            $(window).unload(() => console.log('Clean up any css classes etc. that live edit / sortable has added') );

            console.log('Live Edit Initialized. Using jQuery version: ' + $.fn.jquery);
        });
    });

    // Prevent the user from clicking on things
    // This needs more work as we want them to click on Live Edit ui stuff.
    $(document).ready(() => {
        $(document).on('mousedown', 'btn, button, a, select, input', (event) => {
            event.preventDefault();
            return false;
        });
    });

}($liveEdit));
