/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */

module LiveEdit {
    var $ = $liveedit;

    var componentHelper = LiveEdit.ComponentHelper;

    var _isDragging = false;

    var cursorAt = LiveEdit.ComponentHelper.supportsTouch() ? {left: 15, top: 70} : {left: -10, top: -15};

    var regionSelector = '[data-live-edit-type=region]';

    var layoutSelector = '[data-live-edit-type=layout]';

    var partSelector = '[data-live-edit-type=part]';

    var paragraphSelector = '[data-live-edit-type=paragraph]';

    var itemsToSortSelector = layoutSelector + ',' + partSelector + ',' + paragraphSelector;

    export class DragDropSort {

        constructor() {
            this.createSortable();
            this.registerGlobalListeners();
        }


        public static isDragging() {
            return _isDragging;
        }


        private enableDragDrop():void {
            $(regionSelector).sortable('enable');
        }


        private disableDragDrop():void {
            $(regionSelector).sortable('disable');
        }


        private getDragHelperHtml(text:string):string {
            // Override jQueryUi inline width/height

            return '<div id="live-edit-drag-helper" style="width: 150px; height: 16px;">' +
                '    <img id="live-edit-drag-helper-status-icon" src="../../../admin2/live-edit/images/drop-no.gif"/>' +
                '    <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
                '</div>';
        }


        private setDragHelperText(text:string):void {
            $('#live-edit-drag-helper-text').text(text);
        }


        private createComponentBarDraggables():void {
            var $componentBarComponents = $('.live-edit-component');
            var draggableOptions = {
                connectToSortable: regionSelector,
                addClasses: false,
                cursor: 'move',
                appendTo: 'body',
                zIndex: 5100000,
                // The revert property seems buggy and undocumented.
                // When setting it to 'invalid' the dragged element sometimes reverts when the drop was valid
                // It is possible to use a function that gets a "valid-drop" argument and create your own logic, but the dragged element still reverts
                revert: (validDrop) => {
                },
                cursorAt: cursorAt,
                helper: () => {
                    return this.getDragHelperHtml('');
                },
                start: (event, ui) => {
                    $(window).trigger('dragStart.liveEdit.component', [event, ui]);
                    this.setDragHelperText($(event.target).data('live-edit-component-name'));
                    _isDragging = true;
                },
                stop: (event, ui) => {
                    $(window).trigger('dragStop.liveEdit.component', [event, ui]);
                    _isDragging = false;
                }
            };
            $componentBarComponents.draggable(draggableOptions);
        }


        private createDragHelper(event:JQueryEventObject, helper):string {
            return $(this.getDragHelperHtml(componentHelper.getComponentName(helper)));
        }


        private refreshSortable():void {
            $(regionSelector).sortable('refresh');
        }


        private updateHelperStatusIcon(status:string):void {
            $('#live-edit-drag-helper-status-icon').attr('src', '../../../admin2/live-edit/images/drop-' + status + '.gif');
        }


        private targetIsPlaceholder(target:JQuery):Boolean {
            return target.hasClass('live-edit-drop-target-placeholder')
        }


        private handleSortStart(event:JQueryEventObject, ui):void {
            _isDragging = true;

            // Temporary store the selection info during the drag drop lifecycle.
            // Data is nullified on drag stop.
            var componentIsSelected = ui.item.hasClass('live-edit-selected-component');
            ui.item.data('live-edit-selected-on-sort-start', componentIsSelected);

            var targetComponentName = LiveEdit.ComponentHelper.getComponentName($(event.target));
            ui.placeholder.html('Drop component here' + '<div style="font-size: 10px;">' + targetComponentName + '</div>');

            this.refreshSortable();

            $(window).trigger('sortStart.liveEdit.component', [event, ui]);
        }


        private handleDragOver(event:JQueryEventObject, ui):void {
            event.stopPropagation();

            // todo: Items in component should have the same @data-live-edit-* structure
            var draggedItemIsLayoutComponent = ui.item.data('live-edit-component-type') === 'layout' || ui.item.data('live-edit-type') === 'layout',
                isDraggingOverLayoutComponent = ui.placeholder.closest(layoutSelector).length > 0;

            if (draggedItemIsLayoutComponent && isDraggingOverLayoutComponent) {
                this.updateHelperStatusIcon('no');
                ui.placeholder.hide();
            } else {
                this.updateHelperStatusIcon('yes');
                $(window).trigger('sortOver.liveEdit.component', [event, ui]);
            }
        }

        private handleDragOut(event:JQueryEventObject, ui):void {
            if (this.targetIsPlaceholder($(event.srcElement))) {
                this.removePaddingFromLayoutComponent();
            }

            this.updateHelperStatusIcon('no');
            $(window).trigger('sortOut.liveEdit.component', [event, ui]);
        }

        private handleSortChange(event:JQueryEventObject, ui):void {
            this.addPaddingToLayoutComponent($(event.target));
            this.updateHelperStatusIcon('yes');
            ui.placeholder.show();
            $(window).trigger('sortChange.liveEdit.component', [event, ui]);
        }

        private handleSortUpdate(event:JQueryEventObject, ui):void {
            $(window).trigger('sortUpdate.liveEdit.component', [event, ui]);
        }

        private handleSortStop(event:JQueryEventObject, ui):void {
            _isDragging = false;

            this.removePaddingFromLayoutComponent();

            // todo: Items in component should have the same @data-live-edit-* structure
            var draggedItemIsLayoutComponent = ui.item.data('live-edit-component-type') === 'layout' || ui.item.data('live-edit-type') === 'layout',
                targetIsInLayoutComponent = $(event.target).closest(layoutSelector).length > 0;

            if (draggedItemIsLayoutComponent && targetIsInLayoutComponent) {
                ui.item.remove()
            }


            if (LiveEdit.ComponentHelper.supportsTouch()) {
                $(window).trigger('mouseOut.liveEdit.component');
            }

            var wasSelectedOnDragStart = ui.item.data('live-edit-selected-on-drag-start');

            $(window).trigger('sortStop.liveEdit.component', [event, ui, wasSelectedOnDragStart]);

            ui.item.removeData('live-edit-selected-on-drag-start');
        }

        private handleReceive(event:JQueryEventObject, ui):void {
            if (this.itemIsDraggedFromComponentBar(ui.item)) {
                var $componentBarComponent = $(event.target).children('.live-edit-component');

                console.log($componentBarComponent);

                var componentKey = $componentBarComponent.data('live-edit-component-key');
                var componentType = $componentBarComponent.data('live-edit-component-type');
                var url = '../../../admin2/live-edit/data/mock-component-' + componentKey + '.html';

                console.log(componentKey);

                $componentBarComponent.hide();

                $.ajax({
                    url: url,
                    cache: false
                }).done((html) => {

                        $componentBarComponent.replaceWith(html);

                        // It seems like it is not possible to add new sortables (region in layout) to the existing sortable
                        // So we have to create it again.
                        // Ideally we should destroy the existing sortable first before creating.
                        if (componentType === 'layout') {
                            this.createSortable();
                        }

                        $(window).trigger('sortUpdate.liveEdit.component');
                    });
            }
        }


        private itemIsDraggedFromComponentBar(item:JQuery):Boolean {
            return item.hasClass('live-edit-component');
        }


        private addPaddingToLayoutComponent(component:JQuery):void {
            component.closest(layoutSelector).addClass('live-edit-component-padding');
        }


        private removePaddingFromLayoutComponent():void {
            $('.live-edit-component-padding').removeClass('live-edit-component-padding');
        }


        private registerGlobalListeners():void {
            // The jQuery draggable() is not "live"/support delegates so we have to make sure the components in the component bar are always draggable
            // Make the components in the component bar draggable
            $(window).on('dataLoaded.liveEdit.componentBar', () => {
                this.createComponentBarDraggables();
            });

            $(window).on('select.liveEdit.component', (event, $component) => {
                /*
                 if (LiveEdit.ComponentHelper.supportsTouch()) {
                 enableDragDrop();
                 }
                 */

                /*
                 // When a Layout component is selected it should not be possible to drag any
                 // child components in the layout.
                 // jQuery UI starts dragging the component closest to the mouse target.
                 // Ideally we should update the "items" (to sort) option, but this is unfortunately buggy at the moment(http://bugs.jqueryui.com/ticket/8532)

                 // This is a hack workaround (destroy and re-create sortables) until 8532 is fixed.
                 if (LiveEdit.ComponentHelper.getComponentType($component) === 'layout') {
                 $(regionSelector).sortable('destroy');
                 createSortable(layoutSelector);
                 } else {
                 createSortable(itemsToSortSelector);
                 }
                 */

            });

            $(window).on('deselect.liveEdit.component', () => {
                if (LiveEdit.ComponentHelper.supportsTouch() && !_isDragging) {
                    this.disableDragDrop();
                }
            });

            $(window).on('paragraphSelect.liveEdit.component', () => {
                $(regionSelector).sortable('option', 'cancel', '[data-live-edit-type=paragraph]');
            });

            $(window).on('paragraphLeave.liveEdit.component', () => {
                $(regionSelector).sortable('option', 'cancel', '');
            });
        }


        private createSortable():void {
            $(regionSelector).sortable({
                revert: false,
                connectWith: regionSelector,   // Sortable elements.
                items: itemsToSortSelector,   // Elements to sort.
                distance: 1,
                delay: 150,
                tolerance: 'pointer',
                cursor: 'move',
                cursorAt: cursorAt,
                scrollSensitivity: Math.round(LiveEdit.DomHelper.getViewPortSize().height / 8),
                placeholder: 'live-edit-drop-target-placeholder',
                zIndex: 1001000,
                helper: (event, helper) => this.createDragHelper(event, helper),
                start: (event, ui) => this.handleSortStart(event, ui),  // This event is triggered when sorting starts.
                over: (event, ui) =>  this.handleDragOver(event, ui),   // This event is triggered when a sortable item is moved into a connected list.
                out: (event, ui) =>  this.handleDragOut(event, ui),    // This event is triggered when a sortable item is moved away from a connected list.
                change: (event, ui) => this.handleSortChange(event, ui), // This event is triggered during sorting, but only when the DOM position has changed.
                receive: (event, ui) =>  this.handleReceive(event, ui),
                update: (event, ui) =>  this.handleSortUpdate(event, ui), // This event is triggered when the user stopped sorting and the DOM position has changed.
                stop: (event, ui) =>  this.handleSortStop(event, ui)    // This event is triggered when sorting has stopped.
            });
            // }).disableSelection(); // will not make contenteditable work.
        }

    }
}