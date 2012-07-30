AdminLiveEdit.DragDrop = (function () {
    var isDragging = false;
    var cursorAt = AdminLiveEdit.Util.supportsTouch() ? {left: 15, top: 70} : {left: -15, top: -20};


    function enableDragDrop() {
        $liveedit(AdminLiveEdit.Regions.SELECTOR).sortable('enable');
    }


    function disableDragDrop() {
        $liveedit(AdminLiveEdit.Regions.SELECTOR).sortable('disable');
    }


    function createDragHelper(event, helper) {
        // Inline style is needed in order to keep the width and height while draging an item.
        return $liveedit('<div id="live-edit-drag-helper" style="width: 150px; height: 16px; padding: 6px 8px 6px 8px"><img id="live-edit-drag-helper-status-icon" src="../live-edit/images/drop-yes.gif"/>' +
                         AdminLiveEdit.Util.getPageComponentName(helper) + '</div>');
    }


    function refresh() {
        $liveedit(AdminLiveEdit.Regions.SELECTOR).sortable('refresh');
    }


    function updateHelperStatusIcon(status) {
        $liveedit('#live-edit-drag-helper-status-icon').attr('src', '../live-edit/images/drop-' + status + '.gif');
    }


    function handleDragStart(event, ui) {
        isDragging = true;
        ui.placeholder.html('Drop component here');
        refresh();

        $liveedit.publish('/page/component/dragstart', [event, ui]);
    }


    function handleDragOver(event, ui) {
        updateHelperStatusIcon('yes');
        $liveedit.publish('/page/component/dragover', [event, ui]);
    }


    function handleDragOut(event, ui) {
        updateHelperStatusIcon('no');
        $liveedit.publish('/page/component/dragout', [event, ui]);
    }


    function handleSortChange(event, ui) {
        updateHelperStatusIcon('yes');
        ui.placeholder.show();

        $liveedit.publish('/page/component/sortchange', [event, ui]);
    }


    function handleSortUpdate(event, ui) {
        $liveedit.publish('/page/component/sortupdate', [event, ui]);
    }


    function handleSortStop(event, ui) {
        isDragging = false;

        if (AdminLiveEdit.Util.supportsTouch()) {
            AdminLiveEdit.Highlighter.hide();
        }

        disableDragDrop();

        $liveedit.publish('/page/component/sortstop', [event, ui]);
    }


    function initSubscribers() {
        $liveedit.subscribe('/page/component/select', function () {
            if (AdminLiveEdit.Util.supportsTouch()) {
                enableDragDrop();
            }
        });
        $liveedit.subscribe('/page/component/deselect', function () {
            if (AdminLiveEdit.Util.supportsTouch() && !isDragging) {
                disableDragDrop();
            }
        });
    }


    function init() {
        $liveedit(AdminLiveEdit.Regions.SELECTOR).sortable({
            revert              : 200,
            connectWith         : AdminLiveEdit.Regions.SELECTOR,
            items               : AdminLiveEdit.Windows.SELECTOR,
            distance            : 1,
            delay               : 150,
            tolerance           : 'pointer',
            cursor              : 'move',
            cursorAt            : cursorAt,
            scrollSensitivity   : Math.round(AdminLiveEdit.Util.getViewPortSize().height / 8),
            placeholder         : 'live-edit-drop-target-placeholder',
            helper              : createDragHelper,
            zIndex              : 1001000,
            start               : handleDragStart,
            over                : handleDragOver,
            out                 : handleDragOut,
            change              : handleSortChange,
            update              : handleSortUpdate,
            stop                : handleSortStop
        }).disableSelection();

        disableDragDrop();
        initSubscribers();
    }

    // **********************************************************************************************************************************//
    // Define public methods

    return {

        init: init,

        refresh: refresh,

        enable: enableDragDrop,

        disable: disableDragDrop,

        isDragging: function () {
            return isDragging;
        }

    };

}());




