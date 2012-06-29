AdminLiveEdit.DragDrop = (function () {
    var isDragging = false;
    var cursorAt = AdminLiveEdit.Util.supportsTouch() ? {left : 15, top : 70} : {left : -15, top : -20};


    function enableDragDrop() {
        $liveedit('[data-live-edit-type=region]').sortable('enable');
    }


    function disableDragDrop() {
        $liveedit('[data-live-edit-type=region]').sortable('disable');
    }

    function refresh() {
        $liveedit('[data-live-edit-type=region]').sortable('refresh');
    }


    function updateDragHelperStatusIcon(status) {
        $liveedit('#live-edit-drag-helper-status-icon').attr('src', '../live-edit/images/drop-' + status + '.gif');
    }


    function init() {
        $liveedit('[data-live-edit-type=region]').sortable({
            revert : 200,
            connectWith : '[data-live-edit-type=region]',
            items : '[data-live-edit-type=window]',
            distance : 1,
            delay : 200,
            tolerance : 'pointer',
            cursor : 'pointer',
            cursorAt : cursorAt,
            scrollSensitivity : Math.round(AdminLiveEdit.Util.getViewPortSize().height / 8),
            placeholder : 'live-edit-dd-drop-target-placeholder',
            helper : function (event, helper) {
                return $liveedit('<div id="live-edit-dd-drag-helper" style="width: 150px; height: 16px; padding: 6px 8px 6px 8px"><img id="live-edit-drag-helper-status-icon" src="../live-edit/images/drop-yes.gif"/>' +
                                 helper.attr('data-live-edit-name') + '</div>');
            },

            start : function (event, ui) {
                isDragging = true;
                AdminLiveEdit.ElementSelector.deselect();
                ui.item.show();
                ui.item.css({'opacity' : 0.25});
                ui.item.before(ui.placeholder);
                ui.placeholder.html('Drop component here');

                refresh();
            },

            over : function (event, ui) {
                updateDragHelperStatusIcon('yes');
                AdminLiveEdit.Regions.renderPlaceholdersForEmptyRegions();
            },

            out : function (event, ui) {
                updateDragHelperStatusIcon('no');
                AdminLiveEdit.Regions.renderPlaceholdersForEmptyRegions();
            },

            change : function (event, ui) {
                updateDragHelperStatusIcon('yes');
                ui.placeholder.show();
            },

            update : function (event, ui) {
                AdminLiveEdit.Regions.renderPlaceholdersForEmptyRegions();
            },

            stop : function (event, ui) {
                isDragging = false;
                // Hide the marker after dragging has stopped. As the mouse out won't work on these devices.
                if (AdminLiveEdit.Util.supportsTouch()) {
                    AdminLiveEdit.Highlighter.hide();
                }
                disableDragDrop();

                ui.item.css({'opacity' : ''});
            }

        }).disableSelection();

        disableDragDrop();
    }

    // *****************************************************************************************************************
    // Public

    return {
        refresh : refresh,

        init : init,

        enable : function () {
            enableDragDrop();
        },

        disable : function () {
            disableDragDrop();
        },

        isDragging : function () {
            return isDragging;
        }
    };

}());




