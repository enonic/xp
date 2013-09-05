/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */

module LiveEdit.DragDropSort {

    // Uses
    var $ = $liveEdit;


    var _isDragging:bool = false;

    // Drag helper mouse cursor offsets.
    var cursorAt:any = {left: -10, top: -15};

    // Set up selectors for jQuery.sortable configuration.
    var regionSelector:string = LiveEdit.component.Configuration[LiveEdit.component.Type.REGION].cssSelector;

    var layoutSelector:string = LiveEdit.component.Configuration[LiveEdit.component.Type.LAYOUT].cssSelector;

    var partSelector:string = LiveEdit.component.Configuration[LiveEdit.component.Type.PART].cssSelector;

    var imageSelector:string = LiveEdit.component.Configuration[LiveEdit.component.Type.IMAGE].cssSelector;

    var paragraphSelector:string = LiveEdit.component.Configuration[LiveEdit.component.Type.PARAGRAPH].cssSelector;

    var contextWindowDragSourceSelector:string = '[data-context-window-draggable="true"]';

    var sortableItemsSelector = layoutSelector + ',' + partSelector + ',' + paragraphSelector + ',' + imageSelector;

    export function init():void {
        this.createJQueryUiSortable();
        this.registerGlobalListeners();
    }

    export function isDragging():bool {
        return _isDragging;
    }

    export function enableDragDrop():void {
        $(regionSelector).sortable('enable');
    }

    export function disableDragDrop():void {
        $(regionSelector).sortable('disable');
    }

    export function createJQueryUiSortable():void {
        $(regionSelector).sortable({
            revert: false,
            connectWith: regionSelector,
            items: sortableItemsSelector,
            distance: 1,
            delay: 150,
            tolerance: 'pointer',
            cursor: 'move',
            cursorAt: cursorAt,
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
            connectToSortable: regionSelector,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            zIndex: 5100000,
            cursorAt: cursorAt,
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

    // fixme: can this be shared with live edit Context Window/Components.js ?
    export function createDragHelperHtml(text:string):string {
        // We need to inline the width and height, if not jQuery ui will overwrite it
        var html = '<div id="live-edit-drag-helper" style="width: 150px; height: 28px; position: absolute; z-index: 400000;"><div id="live-edit-drag-helper-inner">' +
            '               <div id="live-edit-drag-helper-status-icon" class="live-edit-drag-helper-no"></div>' +
            '               <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
            '           </div></div>';

        return html;
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
        $(regionSelector).sortable('refresh');
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

        ui.placeholder.html('Drop component here' + '<div style="font-size: 10px;">' + component.getComponentType().getName() + ': ' +  component.getName() + '</div>');

        this.refreshSortable();

        $(window).trigger('sortableStart.liveEdit', [event, ui]);
    }

    export function handleDragOver(event:JQueryEventObject, ui):void {
        event.stopPropagation();

        var component = new LiveEdit.component.Component(ui.item);

        var isDraggingOverLayoutComponent = ui.placeholder.closest(layoutSelector).length > 0;

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
            targetComponentIsInLayoutComponent = $(event.target).closest(layoutSelector).length > 0;

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
            var component = new LiveEdit.component.Component($(event.target).children(contextWindowDragSourceSelector));

            // fixme: is this needed anymore.
            component.getElement().hide(null);

            var placeHolderHtml:string = '';
            placeHolderHtml += '<div class="live-edit-empty-component ' + component.getComponentType().getIconCls() + '" data-live-edit-empty-component="true" data-live-edit-type="' + component.getComponentType().getType() + '"><!-- --></div>';

            component.getElement().replaceWith(placeHolderHtml);

            $(window).trigger('sortableUpdate.liveEdit');


            /*
            if (componentType === LiveEdit.component.Type.IMAGE) {

                var placeHolderHtml:string = '';
                placeHolderHtml += '<div class="live-edit-component-placeholder" data-live-edit-type="' + componentType + '">';
                placeHolderHtml += '    Drop here to upload image';
                placeHolderHtml += '</div>';

                component.getElement().replaceWith(placeHolderHtml);

                $(window).trigger('sortableUpdate.liveEdit');

            } else {
                var url:string = '../../../admin2/live-edit/data/mock-component-' + component.getKey() + '.html';

                $.ajax({
                    url: url,
                    cache: false
                }).done((responseHtml:string) => {
                        component.getElement().replaceWith(responseHtml);

                        // It seems like it is not possible to add new sortables (region in layout) to the existing sortable
                        // So we have to create it again.
                        // Ideally we should destroy the existing sortable first before creating.
                        if (componentType === LiveEdit.component.Type.LAYOUT) {
                            this.createJQueryUiSortable();
                        }

                        $(window).trigger('sortableUpdate.liveEdit');
                    });
            }
            */

        }
    }

    export function isItemDraggedFromContextWindow(item:JQuery):Boolean {
        var isDraggedFromContextWindow:bool = item.data('context-window-draggable');
        return isDraggedFromContextWindow != undefined && isDraggedFromContextWindow == true;
    }

    export function addPaddingToLayoutComponent(component:LiveEdit.component.Component):void {
        component.getElement().closest(layoutSelector).addClass('live-edit-component-padding');
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
            $(regionSelector).sortable('option', 'cancel', LiveEdit.component.Configuration[LiveEdit.component.Type.PARAGRAPH].cssSelector);
        });

        $(window).on('leaveParagraphComponent.liveEdit', () => {
            $(regionSelector).sortable('option', 'cancel', '');
        });
    }

}