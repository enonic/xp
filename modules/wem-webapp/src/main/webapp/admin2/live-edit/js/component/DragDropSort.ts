/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */


module LiveEdit.component.DragDropSort {

    // Uses
    var $ = $liveEdit;

    // jQuery sortable cursor position form to the drag helper.
    var CURSOR_AT:any = {left: 24, top: 24};

    // Set up selectors for jQuery.sortable configuration.
    var REGION_SELECTOR:string = LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.REGION].cssSelector;

    var LAYOUT_SELECTOR:string = LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.LAYOUT].cssSelector;

    var CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR:string = '[data-context-window-draggable="true"]';

    var SORTABLE_ITEMS_SELECTOR:string = createSortableItemsSelector();

    var _isDragging:boolean = false;

    export function init():void {
        this.createJQueryUiSortable();
        this.registerGlobalListeners();
    }

    export function isDragging():boolean {
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
            distance: 20,
            delay: 50,
            tolerance: 'intersect',
            cursor: 'move',
            cursorAt: CURSOR_AT,
            scrollSensitivity: Math.round(LiveEdit.DomHelper.getViewPortSize().height / 8),
            placeholder: 'live-edit-drop-target-placeholder',
            zIndex: 1001001,
            helper:     (event, helper) => LiveEdit.component.helper.DragHelper.createDragHelperHtml(),
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
                return LiveEdit.component.helper.DragHelper.createDragHelperHtml();
            },
            start: (event, ui) => {
                $(window).trigger('draggableStart.liveEdit', [event, ui]);
                _isDragging = true;
            },
            stop: (event, ui) => {
                $(window).trigger('draggableStop.liveEdit', [event, ui]);
                _isDragging = false;
            }
        });
    }

    export function updateHelperStatusIcon(dropAllowed:boolean):void {
        var helper:JQuery = $('#live-edit-drag-helper');
        if (dropAllowed) {
            helper.removeClass("live-edit-font-icon-drop-not-allowed");
            helper.addClass("live-edit-font-icon-drop-allowed");
        } else {
            helper.removeClass("live-edit-font-icon-drop-allowed");
            helper.addClass("live-edit-font-icon-drop-not-allowed");
        }
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
            this.updateHelperStatusIcon(false);
            ui.placeholder.hide();
        } else {
            this.updateHelperStatusIcon(true);
            $(window).trigger('sortableOver.liveEdit', [event, ui]);
        }
    }

    export function handleDragOut(event:JQueryEventObject, ui):void {
        if (this.targetIsPlaceholder($(event.target))) {
            this.removePaddingFromLayoutComponent();
        }
        this.updateHelperStatusIcon(false);
        $(window).trigger('sortableOut.liveEdit', [event, ui]);
    }

    export function handleSortChange(event:JQueryEventObject, ui):void {
        var component = new LiveEdit.component.Component($(event.target))

        this.addPaddingToLayoutComponent(component);
        this.updateHelperStatusIcon(true);
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

            var bogusComponent = new LiveEdit.component.Component($(event.target).children(CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR));

            // fixme: is this needed anymore?
            bogusComponent.getElement().hide(null);

            var emptyElement:JQuery = $(LiveEdit.component.helper.EmptyComponent.createEmptyComponentHtml(bogusComponent));
            var emptyComponent = new LiveEdit.component.Component(emptyElement);

            bogusComponent.getElement().replaceWith(emptyComponent.getElement());

            $(window).trigger('sortableUpdate.liveEdit');

            LiveEdit.component.Selection.select(emptyComponent);
        }
    }

    export function isItemDraggedFromContextWindow(item:JQuery):boolean {
        var isDraggedFromContextWindow:boolean = item.data('context-window-draggable');
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
            $(REGION_SELECTOR).sortable('option', 'cancel', LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.PARAGRAPH].cssSelector);
        });

        $(window).on('leaveParagraphComponent.liveEdit', () => {
            $(REGION_SELECTOR).sortable('option', 'cancel', '');
        });
    }

    export function createSortableItemsSelector():string {
        var config:TypeConfiguration[] = LiveEdit.component.TypeConfiguration;
        var items:string[] = [];

        for (var i = 0; i < config.length; i++) {
            if (config[i].draggable) {
                items.push(config[i].cssSelector);
            }
        }

        return items.toString();
    }

}