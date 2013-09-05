/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */

module LiveEdit.DragDropSort {

    // Uses
    var $ = $liveEdit;

    // jQuery sortable cursor position form to the drag helper.
    var CURSOR_AT:any = {left: -10, top: -15};

    // Set up selectors for jQuery.sortable configuration.
    var REGION_SELECTOR:string = LiveEdit.component.Configuration[LiveEdit.component.Type.REGION].cssSelector;

    var LAYOUT_SELECTOR:string = LiveEdit.component.Configuration[LiveEdit.component.Type.LAYOUT].cssSelector;

    var PART_SELECTOR:string = LiveEdit.component.Configuration[LiveEdit.component.Type.PART].cssSelector;

    var IMAGE_SELECTOR:string = LiveEdit.component.Configuration[LiveEdit.component.Type.IMAGE].cssSelector;

    var PARAGRAPH_SELECTOR:string = LiveEdit.component.Configuration[LiveEdit.component.Type.PARAGRAPH].cssSelector;

    var CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR:string = '[data-context-window-draggable="true"]';

    var SORTABLE_ITEMS_SELECTOR = LAYOUT_SELECTOR + ',' + PART_SELECTOR + ',' + PARAGRAPH_SELECTOR + ',' + IMAGE_SELECTOR;

    var _isDragging:bool = false;


    // fixme: can this be shared with live edit Context Window/Components.js ?
    export function createDragHelperHtml(text:string):string {
        var html:string;
        // Note: The width and height must be inlined so jQueryUI does not overwrite these properties.
        html = '<div id="live-edit-drag-helper" style="width: 150px; height: 28px; position: absolute; z-index: 400000;">' +
               '    <div id="live-edit-drag-helper-inner">' +
               '        <div id="live-edit-drag-helper-status-icon" class="live-edit-drag-helper-no"><!-- --></div>' +
               '        <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
               '    </div>' +
               '</div>';

        return html;
    }

    export function init():void {
        this.createJQueryUiSortable();
        this.registerGlobalListeners();
    }

    export function isDragging():bool {
        return _isDragging;
    }

    export function disableDragDrop():void {
        $(REGION_SELECTOR).sortable('disable');
    }

    export function createJQueryUiSortable():void {
        $(REGION_SELECTOR).sortable({
            revert: false,
            connectWith: REGION_SELECTOR,
            items: SORTABLE_ITEMS_SELECTOR,
            distance: 1,
            delay: 150,
            tolerance: 'pointer',
            cursor: 'move',
            cursorAt: CURSOR_AT,
            scrollSensitivity: Math.round(LiveEdit.DomHelper.getViewPortSize().height / 8),
            placeholder: 'live-edit-drop-target-placeholder',
            zIndex: 1001000,
            helper:     (event, helper) => this.createDragHelper(event, helper),
            start:      (event, ui)     => this.handleSortStart(event, ui),
            over:       (event, ui)     => this.handleDragOver(event, ui),
            out:        (event, ui)     => this.handleDragOut(event, ui),
            change:     (event, ui)     => this.handleSortChange(event, ui),
            receive:    (event, ui)     => this.handleReceive(event, ui),
            update:     (event, ui)     => this.handleSortUpdate(event, ui),
            stop:       (event, ui)     => this.handleSortStop(event, ui)
        });
    }

    // Used by the Context Window when dragging above the IFrame
    export function createJQueryUiDraggable(contextWindowItem:JQuery):void {
        contextWindowItem.draggable({
            connectToSortable: REGION_SELECTOR,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            zIndex: 5100000,
            cursorAt: CURSOR_AT,
            helper: () => {
                return createDragHelperHtml('');
            },
            start: (event, ui) => {
                $(window).trigger('draggableStart.liveEdit', [event, ui]);
                this.setDragHelperText($(event.target).data('live-edit-name'));
                _isDragging = true;
            },
            stop: (event, ui) => {
                $(window).trigger('draggableStop.liveEdit', [event, ui]);
                _isDragging = false;
            }
        });
    }

    export function createDragHelper(event:JQueryEventObject, helperElement:JQuery):string {
        var component = new LiveEdit.component.Component(helperElement);
        return $(createDragHelperHtml(component.getName()));
    }

    export function setDragHelperText(text:string):void {
        $('#live-edit-drag-helper-text').text(text);
    }

    export function setHelperStatusIcon(status:string):void {
        $('#live-edit-drag-helper-status-icon').attr('class', 'live-edit-drag-helper-' + status);
    }

    export function refreshSortable():void {
        $(REGION_SELECTOR).sortable('refresh');
    }

    export function targetIsPlaceholder(target:JQuery):Boolean {
        return target.hasClass('live-edit-drop-target-placeholder')
    }

    export function handleSortStart(event:JQueryEventObject, ui):void {
        _isDragging = true;

        var component = new LiveEdit.component.Component(ui.item);

        // Temporary store the selection info during the drag drop lifecycle.
        // Data is nullified on drag stop.

        ui.item.data('live-edit-selected-on-sort-start', component.isSelected());

        ui.placeholder.html(LiveEdit.PlaceholderCreator.createPlaceholderForJQuerySortable(component));

        this.refreshSortable();

        $(window).trigger('sortableStart.liveEdit', [event, ui]);
    }

    export function handleDragOver(event:JQueryEventObject, ui):void {
        event.stopPropagation();

        var component = new LiveEdit.component.Component(ui.item);

        var isDraggingOverLayoutComponent = ui.placeholder.closest(LAYOUT_SELECTOR).length > 0;

        if (component.getComponentType().getType() === LiveEdit.component.Type.LAYOUT && isDraggingOverLayoutComponent) {
            this.setHelperStatusIcon('no');
            ui.placeholder.hide();
        } else {
            this.setHelperStatusIcon('yes');
            $(window).trigger('sortableOver.liveEdit', [event, ui]);
        }
    }

    export function handleDragOut(event:JQueryEventObject, ui):void {
        if (this.targetIsPlaceholder($(event.srcElement))) {
            this.removePaddingFromLayoutComponent();
        }
        this.setHelperStatusIcon('no');
        $(window).trigger('sortableOut.liveEdit', [event, ui]);
    }

    export function handleSortChange(event:JQueryEventObject, ui):void {
        var component = new LiveEdit.component.Component($(event.target))

        this.addPaddingToLayoutComponent(component);
        this.setHelperStatusIcon('yes');
        ui.placeholder.show(null);

        $(window).trigger('sortableChange.liveEdit', [event, ui]);
    }

    export function handleSortUpdate(event:JQueryEventObject, ui):void {
        $(window).trigger('sortableUpdate.liveEdit', [event, ui]);
    }

    export function handleSortStop(event:JQueryEventObject, ui):void {
        _isDragging = false;

        var component = new LiveEdit.component.Component(ui.item);

        this.removePaddingFromLayoutComponent();

        var draggedItemIsLayoutComponent = component.getComponentType().getType() === LiveEdit.component.Type.LAYOUT,
            targetComponentIsInLayoutComponent = $(event.target).closest(LAYOUT_SELECTOR).length > 0;

        if (draggedItemIsLayoutComponent && targetComponentIsInLayoutComponent) {
            ui.item.remove()
        }

        if (LiveEdit.DomHelper.supportsTouch()) {
            $(window).trigger('mouseOutComponent.liveEdit');
        }

        var wasSelectedOnDragStart = component.getElement().data('live-edit-selected-on-drag-start');

        $(window).trigger('sortableStop.liveEdit', [event, ui, wasSelectedOnDragStart]);

        component.getElement().removeData('live-edit-selected-on-drag-start');
    }

    // When sortable receives a new item
    export function handleReceive(event:JQueryEventObject, ui):void {
        if (this.isItemDraggedFromContextWindow(ui.item)) {
            var component = new LiveEdit.component.Component($(event.target).children(CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR));

            // fixme: is this needed anymore?
            component.getElement().hide(null);

            component.getElement().replaceWith(LiveEdit.PlaceholderCreator.createEmptyComponentElement(component));

            $(window).trigger('sortableUpdate.liveEdit');
        }
    }

    export function isItemDraggedFromContextWindow(item:JQuery):Boolean {
        var isDraggedFromContextWindow:bool = item.data('context-window-draggable');
        return isDraggedFromContextWindow != undefined && isDraggedFromContextWindow == true;
    }

    export function addPaddingToLayoutComponent(component:LiveEdit.component.Component):void {
        component.getElement().closest(LAYOUT_SELECTOR).addClass('live-edit-component-padding');
    }


    export function removePaddingFromLayoutComponent():void {
        $('.live-edit-component-padding').removeClass('live-edit-component-padding');
    }

    export function registerGlobalListeners():void {
        $(window).on('deselectComponent.liveEdit', () => {
            if (LiveEdit.DomHelper.supportsTouch() && !_isDragging) {
                this.disableDragDrop();
            }
        });

        $(window).on('selectParagraphComponent.liveEdit', () => {
            $(REGION_SELECTOR).sortable('option', 'cancel', LiveEdit.component.Configuration[LiveEdit.component.Type.PARAGRAPH].cssSelector);
        });

        $(window).on('leaveParagraphComponent.liveEdit', () => {
            $(REGION_SELECTOR).sortable('option', 'cancel', '');
        });
    }

}