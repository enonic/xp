AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.DragDropSort');
/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */
AdminLiveEdit.DragDropSort = (function () {
    'use strict';

    var util = AdminLiveEdit.Util;

    var isDragging = false;

    var cursorAt = AdminLiveEdit.Util.supportsTouch() ? {left: 15, top: 70} : {left: -10, top: -15};

    var regionSelector = '[data-live-edit-type=region]';

    var itemsToSortSelector = '[data-live-edit-type=layout], [data-live-edit-type=part], [data-live-edit-type=paragraph]';


    function enableDragDrop() {
        $liveedit('').sortable('enable');
    }


    function disableDragDrop() {
        $liveedit(regionSelector).sortable('disable');
    }


    function getDragHelperHtml(text) {
        // Override jQueryUi inline width/height
        return '<div id="live-edit-drag-helper" style="width: 150px; height: 16px;">' +
               '    <img id="live-edit-drag-helper-status-icon" src="../app/images/drop-no.gif"/>' +
               '    <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
               '</div>';
    }


    function setDragHelperText(text) {
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
                return getDragHelperHtml('');
            },
            start: function (event, ui) {
                $liveedit(window).trigger('component.onDragStart', [event, ui]);
                setDragHelperText($liveedit(event.target).data('live-edit-component-name'));
                isDragging = true;
            },
            stop: function (event, ui) {
                $liveedit(window).trigger('component.onDragStop', [event, ui]);
                isDragging = false;
            }
        };
        $componentBarComponents.draggable(draggableOptions);
    }


    function createDragHelper(event, helper) {
        return $liveedit(getDragHelperHtml(util.getComponentName(helper)));
    }


    function refreshSortable() {
        $liveedit(regionSelector).sortable('refresh');
    }


    function updateHelperStatusIcon(status) {
        $liveedit('#live-edit-drag-helper-status-icon').attr('src', '../app/images/drop-' + status + '.gif');
    }


    function targetIsPlaceholder($target) {
        return $target.hasClass('live-edit-drop-target-placeholder')
    }


    function handleSortStart(event, ui) {
        isDragging = true;

        // Temporary store the selection info during the drag drop lifecycle.
        // Data is nullified on drag stop.

        var componentIsSelected = ui.item.hasClass('live-edit-selected-component');
        ui.item.data('live-edit-selected-on-sort-start', componentIsSelected);

        ui.placeholder.text('Drop component here');
        refreshSortable();

        $liveedit(window).trigger('component.onSortStart', [event, ui]);
    }

    function handleDragOver(event, ui) {
        updateHelperStatusIcon('yes');
        $liveedit(window).trigger('component:sort:over', [event, ui]);
    }

    function handleDragOut(event, ui) {
        if (targetIsPlaceholder($liveedit(event.srcElement))) {
            removePaddingFromLayoutComponent();
        }

        updateHelperStatusIcon('no');
        $liveedit(window).trigger('component:sort:out', [event, ui]);
    }

    function handleSortChange(event, ui) {
        addPaddingToLayoutComponent($(event.target));
        updateHelperStatusIcon('yes');
        ui.placeholder.show();
        $liveedit(window).trigger('component:sort:change', [event, ui]);
    }

    function handleSortUpdate(event, ui) {
        $liveedit(window).trigger('component.onSortUpdate', [event, ui]);
    }

    function handleSortStop(event, ui) {
        isDragging = false;

        if (AdminLiveEdit.Util.supportsTouch()) {
            $liveedit(window).trigger('component.mouseOut');
        }

        var wasSelectedOnDragStart = ui.item.data('live-edit-selected-on-drag-start');

        $liveedit(window).trigger('component.onSortStop', [event, ui, wasSelectedOnDragStart]);

        ui.item.removeData('live-edit-selected-on-drag-start');
    }


    function itemIsDraggedFromComponentBar(item) {
        return item.hasClass('live-edit-component');
    }


    function handleReceive(event, ui) {

        if (itemIsDraggedFromComponentBar(ui.item)) {
            var $componentBarComponent = $liveedit(this).children('.live-edit-component');
            var componentKey = $componentBarComponent.data('live-edit-component-key');
            var componentType = $componentBarComponent.data('live-edit-component-type');
            var url = '../app/data/mock-component-' + componentKey + '.html';

            $componentBarComponent.hide();

            $liveedit.ajax({
                url: url,
                cache: false
            }).done(function (html) {

                    $componentBarComponent.replaceWith(html);

                    // It seems like it is not possible to add new sortables (region in layout) to the existing sortable
                    // So we have to create it again.
                    // Ideally we should destroy the existing sortable first before creating.
                    if (componentType === 'layout') {
                        createSortable();
                    }

                    $liveedit(window).trigger('component.onSortUpdate');
                });
        }
    }


    function addPaddingToLayoutComponent($component) {
        $liveedit(event.target).closest('[data-live-edit-type=layout]').addClass('live-edit-component-padding');
    }


    function removePaddingFromLayoutComponent() {
        $liveedit('.live-edit-component-padding').removeClass('live-edit-component-padding');
    }


    function registerGlobalListeners() {
        // The jQuery draggable() is not "live"/support delegates so we have to make sure the components in the component bar are always draggable
        // Make the components in the component bar draggable
        $liveedit(window).on('componentBar.dataLoaded', function () {
            createComponentBarDraggables();
        });

        $liveedit(window).on('component.onSelect', function (event, $component) {
            /*
             if (AdminLiveEdit.Util.supportsTouch()) {
             enableDragDrop();
             }
             */

            /*
             // When a Layout component is selected it should not be possible to drag any
             // child components in the layout.
             // jQuery UI starts dragging the component closest to the mouse target.
             // Ideally we should update the "items" (to sort) option, but this is unfortunately buggy at the moment(http://bugs.jqueryui.com/ticket/8532)

             // This is a hack workaround (destroy and re-create sortables) until 8532 is fixed.
             if (AdminLiveEdit.Util.getComponentType($component) === 'layout') {
             $liveedit(regionSelector).sortable('destroy');
             createSortable('[data-live-edit-type=layout]');
             } else {
             createSortable(itemsToSortSelector);
             }
             */

        });

        $liveedit(window).on('component.onDeselect', function () {
            if (AdminLiveEdit.Util.supportsTouch() && !isDragging) {
                disableDragDrop();
            }
        });

        $liveedit(window).on('component.onParagraphSelect', function () {
            $liveedit(regionSelector).sortable('option', 'cancel', '[data-live-edit-type=paragraph]');
        });

        $liveedit(window).on('component.onParagraphEditLeave', function () {
            $liveedit(regionSelector).sortable('option', 'cancel', '');
        });
    }


    function createSortable() {
        $liveedit(regionSelector).sortable({
            revert: false,
            connectWith: regionSelector,   // Sortable elements.
            items: itemsToSortSelector,   // Elements to sort.
            distance: 1,
            delay: 150,
            tolerance: 'pointer',
            cursor: 'move',
            cursorAt: cursorAt,
            scrollSensitivity: Math.round(AdminLiveEdit.Util.getViewPortSize().height / 8),
            placeholder: 'live-edit-drop-target-placeholder',
            helper: createDragHelper,
            zIndex: 1001000,
            start: handleSortStart,  // This event is triggered when sorting starts.
            over: handleDragOver,   // This event is triggered when a sortable item is moved into a connected list.
            out: handleDragOut,    // This event is triggered when a sortable item is moved away from a connected list.
            change: handleSortChange, // This event is triggered during sorting, but only when the DOM position has changed.
            receive: handleReceive,
            update: handleSortUpdate, // This event is triggered when the user stopped sorting and the DOM position has changed.
            stop: handleSortStop    // This event is triggered when sorting has stopped.
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
        initialize: init,
        enable: enableDragDrop,
        disable: disableDragDrop,
        isDragging: function () {
            return isDragging;
        }
    };

}());
