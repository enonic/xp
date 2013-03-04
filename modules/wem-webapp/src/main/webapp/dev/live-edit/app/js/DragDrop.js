/*
    TODO: Refactor to a dynamic object!
*/
AdminLiveEdit.DragDrop = (function () {
    'use strict';

    var util = AdminLiveEdit.Util;

    var isDragging = false;
    var cursorAt = AdminLiveEdit.Util.supportsTouch() ? {left: 15, top: 70} : {left: -15, top: -20};

    var regionSelector = '[data-live-edit-type=region]';
    // var itemsToSort = '[data-live-edit-type=part], [data-live-edit-type=paragraph]';
    var itemsToSort = '[data-live-edit-type=part]';


    function enableDragDrop() {
        $liveedit('').sortable('enable');
    }


    function disableDragDrop() {
        $liveedit(regionSelector).sortable('disable');
    }


    function getHelperHtml(text) {
        // Override jQueryUi inline width/height
        return '<div id="live-edit-drag-helper" style="width: 150px; height: 16px;">' +
               '    <img id="live-edit-drag-helper-status-icon" src="../app/images/drop-no.gif"/>' +
               '    <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
               '</div>';
    }


    function setHelperText(text) {
        $liveedit('#live-edit-drag-helper-text').text(text);
    }


    function createComponentBarDraggables() {
        var $componentBarComponents = $liveedit('.live-edit-component');

        var draggableOptions = {
            connectToSortable: regionSelector,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            zIndex: 5100000,
            // The revert property seems buggy and undocumented.
            // When setting it to 'invalid' the dragged element sometimes reverts when the drop was valid
            // It is possible to use a function that gets a "valid-drop" argument and create your own logic, but the dragged element still reverts

            revert: function (validDrop) {
            },
            cursorAt: cursorAt,
            helper: function () {
                return getHelperHtml('');
            },
            start: function (event, ui) {
                $liveedit(window).trigger('component:drag:start', [event, ui]);
                setHelperText($liveedit(event.target).data('live-edit-component-name'));
                isDragging = true;
            },
            stop: function (event, ui) {
                $liveedit(window).trigger('component:drag:stop', [event, ui]);
                isDragging = false;
            }
        };

        $componentBarComponents.draggable(draggableOptions);
    }


    function createDragHelper(event, helper) {
        return $liveedit(getHelperHtml(util.getComponentName(helper)));
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

        $liveedit(window).trigger('component:sort:start', [event, ui]);
    }


    function handleDragOver(event, ui) {
        updateHelperStatusIcon('yes');
        $liveedit(window).trigger('component:sort:over', [event, ui]);
    }


    function handleDragOut(event, ui) {
        updateHelperStatusIcon('no');
        $liveedit(window).trigger('component:sort:out', [event, ui]);
    }


    function handleSortChange(event, ui) {
        updateHelperStatusIcon('yes');
        ui.placeholder.show();
        $liveedit(window).trigger('component:sort:change', [event, ui]);
    }


    function handleSortUpdate(event, ui) {
        $liveedit(window).trigger('component:sort:update', [event, ui]);
    }


    function handleSortStop(event, ui) {
        isDragging = false;

        if (AdminLiveEdit.Util.supportsTouch()) {
            $liveedit(window).trigger('component:mouseout');
        }

        var wasSelectedOnDragStart = ui.item.data('live-edit-selected-on-drag-start');

        $liveedit(window).trigger('component:sort:stop', [event, ui, wasSelectedOnDragStart]);

        ui.item.removeData('live-edit-selected-on-drag-start');
    }


    function itemIsDraggedFromComponentBar(item) {
        return item.hasClass('live-edit-component');
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
                $liveedit(window).trigger('component:sort:update');
            });

        }
    }


    function registerGlobalListeners() {
        // The jQuery draggable() is not "live"/support delegates so we have to make sure the components in the component bar are always draggable
        // Make the components in the component bar draggable
        $liveedit(window).on('componentBar:dataLoaded', function () {
            createComponentBarDraggables();
        });

        $liveedit(window).on('component:click:select', function () {
            if (AdminLiveEdit.Util.supportsTouch()) {
                enableDragDrop();
            }
        });

        $liveedit(window).on('component:click:deselect', function () {
            if (AdminLiveEdit.Util.supportsTouch() && !isDragging) {
                disableDragDrop();
            }
        });

        $liveedit(window).on('component:paragraph:edit', function () {
        });

        $liveedit(window).on('component:paragraph:close', function () {
        });
    }


    function createSortable () {
        $liveedit(regionSelector).sortable({
            revert              : 1000,
            connectWith         : regionSelector,   // Sortable elements.
            items               : itemsToSort,   // Elements to sort.
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
        });
        // }).disableSelection(); // will not make contenteditable work.
    }


    function init() {
        createSortable();
        registerGlobalListeners();
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
