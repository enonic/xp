/*
    TODO: Refactor to a dynamic object!
*/
AdminLiveEdit.DragDrop = (function () {
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


    function createComponentBarDraggables() {
        var draggableOptions = {
            connectToSortable: regionSelector,
            cursor: 'move',
            revert: function (dropped) {
                // console.log(dropped);
            },
            helper: function () {
                return '<div style="width: 200px; height: 20px; background-color: #ccc; padding: 10px;">Helper</div>';
            },
            start: function () {
                isDragging = true;
            },
            stop: function () {
                isDragging = false;
            }
        };

        $liveedit('.live-edit-component').draggable(draggableOptions);
    }


    function createDragHelper(event, helper) {
        // Inline style is needed in order to keep the width and height while draging an item.
        return $liveedit('<div id="live-edit-drag-helper" style="width: 150px; height: 16px;">' +
                         '  <img id="live-edit-drag-helper-status-icon" src="../app/images/drop-yes.gif"/>' +
                         util.getComponentName(helper) +
                         '</div>');
    }


    function refresh() {
        $liveedit(regionSelector).sortable('refresh');
    }


    function updateHelperStatusIcon(status) {
        $liveedit('#live-edit-drag-helper-status-icon').attr('src', '../app/images/drop-' + status + '.gif');
    }


    function handleSortStart(event, ui) {
        isDragging = true;

        // Temporary store the selection info during the drag drop lifecycle.
        // Data is nullified on drag stop.
        var componentIsSelected = ui.item.hasClass('live-edit-selected-component');
        ui.item.data('live-edit-selected-on-sort-start', componentIsSelected);

        ui.placeholder.text('Drop component here');
        refresh();

        $liveedit(window).trigger('component:drag:start', [event, ui]);
    }


    function handleDragOver(event, ui) {
        updateHelperStatusIcon('yes');
        $liveedit(window).trigger('component:drag:over', [event, ui]);
    }


    function handleDragOut(event, ui) {
        updateHelperStatusIcon('no');
        $liveedit(window).trigger('component:drag:out', [event, ui]);
    }


    function handleSortChange(event, ui) {
        updateHelperStatusIcon('yes');
        ui.placeholder.show();
        $liveedit(window).trigger('component:drag:change', [event, ui]);
    }


    function handleSortUpdate(event, ui) {
        $liveedit(window).trigger('component:drag:update', [event, ui]);
    }


    function handleSortStop(event, ui) {
        isDragging = false;

        if (AdminLiveEdit.Util.supportsTouch()) {
            $liveedit(window).trigger('component:mouseout');
        }

        // Added on sort start
        var wasSelectedOnDragStart = ui.item.data('live-edit-selected-on-drag-start');
        $liveedit(window).trigger('component:drag:stop', [event, ui, wasSelectedOnDragStart]);

        ui.item.removeData('live-edit-selected-on-drag-start');
    }

    function handleReceive(event, ui) {
        if (itemIsDraggedFromComponentBar(ui.item)) {
            var $component = $liveedit(this).children('.live-edit-component');
            var componentKey = $component.data('live-edit-component-key');
            var url = '../app/data/mock-component-' + componentKey + '.html';

            $component.hide();

            $liveedit.ajax({
                url: url,
                cache: false
            }).done(function (html) {
                $component.replaceWith(html);
            });

        }
    }


    function initSubscribers() {

        $liveedit(window).on('componentBar:dataLoaded', function () {
            createComponentBarDraggables();
        });

        $liveedit(window).on('component:select', function () {
            if (AdminLiveEdit.Util.supportsTouch()) {
                enableDragDrop();
            }
        });

        $liveedit(window).on('component:deselect', function () {
            if (AdminLiveEdit.Util.supportsTouch() && !isDragging) {
                disableDragDrop();
            }
        });
    }


    function itemIsDraggedFromComponentBar(item) {
        return item.hasClass('live-edit-component');
    }


    function createDummyWindowHtml() {
        var dummyKey = new Date().getTime(),
            html = '';

        html += '<div data-live-edit-type="window" data-live-edit-key="' + dummyKey + '" data-live-edit-name="HTML 5 Video">';
        html += '<video width="100%" id="tag" poster="http://content.bitsontherun.com/thumbs/q1fx20VZ-720.jpg" preload="none" controls>';
        html += '	<source src="http://content.bitsontherun.com/videos/q1fx20VZ-52qL9xLP.mp4" type="video/mp4" />';
        html += '	<source src="http://content.bitsontherun.com/videos/q1fx20VZ-27m5HpIu.webm" type="video/webm" />';
        html += '	<p class="warning">Your browser does not support HTML5 video.</p>';
        html += '</video>';
        html += '</div>';

        return html;
    }


    function init() {
        $liveedit(regionSelector).sortable({
            revert              : true,
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
            receive             : handleReceive,
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
