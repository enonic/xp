AdminLiveEdit.ui.DragDrop = (function () {
    var isDragging = false;
    var cursorAt = AdminLiveEdit.Util.supportsTouch() ? {left: 15, top: 70} : {left: -15, top: -20};


    function enableDragDrop() {
        $liveedit(AdminLiveEdit.components.RegionComponents.SELECTOR).sortable('enable');
    }


    function disableDragDrop() {
        $liveedit(AdminLiveEdit.components.RegionComponents.SELECTOR).sortable('disable');
    }


    function createDragHelper(event, helper) {
        // Inline style is needed in order to keep the width and height while draging an item.
        return $liveedit('<div id="live-edit-drag-helper" style="width: 150px; height: 16px; padding: 6px 8px 6px 8px"><img id="live-edit-drag-helper-status-icon" src="../live-edit/images/drop-yes.gif"/>' +
                         AdminLiveEdit.Util.getComponentName(helper) + '</div>');
    }


    function refresh() {
        $liveedit(AdminLiveEdit.components.RegionComponents.SELECTOR).sortable('refresh');
    }


    function updateHelperStatusIcon(status) {
        $liveedit('#live-edit-drag-helper-status-icon').attr('src', '../resources/images/drop-' + status + '.gif');
    }


    function handleSortStart(event, ui) {
        isDragging = true;
        ui.placeholder.html('Drop component here');
        refresh();

        $liveedit.publish('/page/component/sortstart', [event, ui]);
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
            $liveedit.publish('/page/component/hide-highlighter');
        }

        // disableDragDrop();
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
        $liveedit(AdminLiveEdit.components.RegionComponents.SELECTOR).sortable({
            revert              : 200,
            connectWith         : AdminLiveEdit.components.RegionComponents.SELECTOR,   // Sortable elements.
            items               : AdminLiveEdit.components.WindowComponents.SELECTOR,   // Elements to sort.
            distance            : 1,
            delay               : 150,
            tolerance           : 'pointer',
            cursor              : 'move',
            cursorAt            : cursorAt,
            scrollSensitivity   : Math.round(AdminLiveEdit.Util.getViewPortSize().height / 8),
            placeholder         : 'live-edit-drop-target-placeholder',
            helper              : createDragHelper,
            zIndex              : 1001000,
            start               : handleSortStart,  // This event is triggered when sorting starts.
            over                : handleDragOver,   // This event is triggered when a sortable item is moved into a connected list.
            out                 : handleDragOut,    // This event is triggered when a sortable item is moved away from a connected list.
            change              : handleSortChange, // This event is triggered during sorting, but only when the DOM position has changed.
            update              : handleSortUpdate, // This event is triggered when the user stopped sorting and the DOM position has changed.
            stop                : handleSortStop    // This event is triggered when sorting has stopped.
        }).disableSelection();

        // disableDragDrop();

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




