AdminLiveEdit.DragDrop = function()
{
    var isDragging = false;
    var cursorAt = AdminLiveEdit.Util.supportsTouch() ? {left: 15, top: 70} : {left: -15, top: -20};

    function init()
    {
        $liveedit('[data-live-edit-type=region]').sortable({
            revert: 200,
            connectWith: '[data-live-edit-type=region]',
            items: '[data-live-edit-type=window]',
            distance: 1,
            tolerance: 'pointer',
            cursor: 'pointer',
            cursorAt: cursorAt,
            scrollSensitivity: Math.round(AdminLiveEdit.Util.getViewPortSize().height / 8),
            placeholder: 'live-edit-dd-drop-target-placeholder',
            helper: function (event, helper) {
                return $liveedit('<div id="live-edit-dd-drag-helper" style="width: 150px; height: 16px; padding: 6px 8px 6px 8px"><img id="live-edit-drag-helper-status-icon" src="live-edit/images/drop-yes.gif"/>' + helper.attr('data-live-edit-name') + '</div>' );
            },

            start: function (event, ui) {
                isDragging = true;
                AdminLiveEdit.ElementSelector.deselect();
                ui.item.show();
                ui.item.css({'opacity': 0.25});
                ui.item.before(ui.placeholder);
                ui.placeholder.html('Drop component here');

                refresh();
            },

            over: function (event, ui) {
                updateDragHelperStatusIcon('yes');
                AdminLiveEdit.Regions.renderPlaceholders();
            },

            out: function (event, ui) {
                updateDragHelperStatusIcon('no');
                AdminLiveEdit.Regions.renderPlaceholders();
            },

            change: function (event, ui) {
                updateDragHelperStatusIcon('yes');
                ui.placeholder.show();
            },

            update: function (event, ui) {
                AdminLiveEdit.Regions.renderPlaceholders();
            },

            stop: function (event, ui) {
                isDragging = false;
                // Hide the marker after dragging has stopped. As the mouse out won't work on these devices.
                if ( AdminLiveEdit.Util.supportsTouch() ) {
                    AdminLiveEdit.Highlighter.hide();
                }
                ui.item.css({'opacity': ''});
            }

        }).disableSelection();
    }


    function refresh()
    {
        $liveedit('[data-live-edit-type=region]').sortable('refresh');
    }


    function updateDragHelperStatusIcon(status)
    {
        $liveedit('#live-edit-drag-helper-status-icon' ).attr('src', 'live-edit/images/drop-'+status+'.gif');
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        refresh: refresh,

        init: init,

        isDragging: function() {
            return isDragging;
        }
    }

}();




