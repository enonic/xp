AdminLiveEdit.ui.DragDrop = (function () {
    'use strict';

    var util = AdminLiveEdit.Util;

    var isDragging = false;
    var cursorAt = AdminLiveEdit.Util.supportsTouch() ? {left: 15, top: 70} : {left: -15, top: -20};

    var regionSelector = '[data-live-edit-type=region]';
    var windowSelector = '[data-live-edit-type=window]';


    function enableDragDrop() {
        $liveedit('').sortable('enable');
    }


    function disableDragDrop() {
        $liveedit(regionSelector).sortable('disable');
    }


    function createDragHelper(event, helper) {
        // Inline style is needed in order to keep the width and height while draging an item.
        return $liveedit('<div id="live-edit-drag-helper" style="width: 150px; height: 16px; padding: 6px 8px 6px 8px"><img id="live-edit-drag-helper-status-icon" src="../resources/images/drop-yes.gif"/>' +
                         util.getComponentName(helper) + '</div>');
    }


    function refresh() {
        $liveedit(regionSelector).sortable('refresh');
    }


    function updateHelperStatusIcon(status) {
        $liveedit('#live-edit-drag-helper-status-icon').attr('src', '../resources/images/drop-' + status + '.gif');
    }


    function handleSortStart(event, ui) {
        isDragging = true;

        // Temporary store the selection info during the drag drop lifecycle.
        // Data is nullified on drag stop.
        var componentIsSelected = ui.item.hasClass('live-edit-selected-component');
        ui.item.data('live-edit-selected-on-sort-start', componentIsSelected);

        ui.placeholder.text('Drop component here');
        refresh();

        $liveedit.publish('/ui/dragdrop/on-sortstart', [event, ui]);
    }


    function handleDragOver(event, ui) {
        updateHelperStatusIcon('yes');
        $liveedit.publish('/ui/dragdrop/on-dragover', [event, ui]);
    }


    function handleDragOut(event, ui) {
        updateHelperStatusIcon('no');
        $liveedit.publish('/ui/dragdrop/on-dragout', [event, ui]);
    }


    function handleSortChange(event, ui) {
        updateHelperStatusIcon('yes');
        ui.placeholder.show();

        $liveedit.publish('/ui/dragdrop/on-sortchange', [event, ui]);
    }


    function handleSortUpdate(event, ui) {
        $liveedit.publish('/ui/dragdrop/on-sortupdate', [event, ui]);
    }


    function handleSortStop(event, ui) {
        isDragging = false;

        if (AdminLiveEdit.Util.supportsTouch()) {
            $liveedit.publish('/ui/highlighter/on-hide');
        }

        // Added on sort start
        var wasSelectedOnSortStart = ui.item.data('live-edit-selected-on-sort-start');

        $liveedit.publish('/ui/dragdrop/on-sortstop', [event, ui, wasSelectedOnSortStart]);

        ui.item.removeData('live-edit-selected-on-sort-start');
    }


    function initSubscribers() {
        $liveedit.subscribe('/ui/componentselector/on-select', function () {
            if (AdminLiveEdit.Util.supportsTouch()) {
                enableDragDrop();
            }
        });

        $liveedit.subscribe('/ui/componentselector/on-deselect', function () {
            if (AdminLiveEdit.Util.supportsTouch() && !isDragging) {
                disableDragDrop();
            }
        });
    }


    function init() {
        $liveedit(regionSelector).sortable({
            revert              : 200,
            connectWith         : regionSelector,   // Sortable elements.
            items               : windowSelector,   // Elements to sort.
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
