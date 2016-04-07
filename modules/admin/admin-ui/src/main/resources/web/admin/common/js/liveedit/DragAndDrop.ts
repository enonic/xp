module api.liveedit {

    import PropertyTree = api.data.PropertyTree;
    import Component = api.content.page.region.Component;
    import ComponentName = api.content.page.region.ComponentName;
    import ComponentType = api.content.page.region.ComponentType;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import DescriptorBasedComponentBuilder = api.content.page.region.DescriptorBasedComponentBuilder;

    import DragHelper = api.ui.DragHelper;

    export class DragAndDrop {

        public static debug = false;

        private static _messageCounter: number = 0;

        private static instance: DragAndDrop;

        private pageView: PageView;

        // Set up selectors for jQuery.sortable configuration.
        public REGION_SELECTOR: string = RegionItemType.get().getConfig().getCssSelector();

        public ITEM_NOT_DRAGGABLE_SELECTOR: string = '.not-draggable';

        public PLACEHOLDER_CONTAINER_SELECTOR: string = 'live-edit-drag-placeholder-container';

        public DRAGGED_OVER_CLASS: string = 'dragged-over';

        public DRAGGING_ACTIVE_CLASS: string = 'dragging';

        public SORTABLE_ITEMS_SELECTOR: string;

        private _isDragging: boolean = false;

        private _wasDropped: boolean = false;

        private _wasDestroyed: boolean = false;

        private _newItemItemType: ItemType;

        private _draggedComponentView: ComponentView<Component>;

        private dragStartedListeners: {(componentView: ComponentView<Component>): void}[] = [];
        private dragStoppedListeners: {(componentView: ComponentView<Component>): void}[] = [];
        private droppedListeners: {(componentView: ComponentView<Component>, regionView: RegionView): void}[] = [];
        private canceledListeners: {(componentView: ComponentView<Component>): void}[] = [];


        public static init(pageView: PageView) {
            DragAndDrop.instance = new DragAndDrop(pageView);
        }


        public static get(): DragAndDrop {
            if (!DragAndDrop.instance) {
                throw Error('Do DragAndDrop.init(pageView) first');
            }
            return DragAndDrop.instance;
        }

        constructor(pageView: PageView) {
            this.pageView = pageView;
            this.SORTABLE_ITEMS_SELECTOR = this.createSortableItemsSelector();
            this.createSortable(this.REGION_SELECTOR);
        }

        isDragging(): boolean {
            return this._isDragging;
        }

        createSortableLayout(component: ItemView) {
            wemjq(component.getHTMLElement()).find(this.REGION_SELECTOR).each((index, element) => {
                this.createSortable(wemjq(element));
            });
        }

        refreshSortable(): void {
            wemjq(this.REGION_SELECTOR).sortable('refresh');
        }


        private processMouseOverRegionView(regionView: RegionView) {

            // Make sure no other region has the over class
            this.pageView.getItemViewsByType(RegionItemType.get()).forEach((region: RegionView) => {
                region.toggleClass(this.DRAGGED_OVER_CLASS, region.getRegionPath().equals(regionView.getRegionPath()));
            });

            // Need to update empty state if the only item
            // has been dragged out of the region (marked as moving)
            regionView.refreshEmptyState();

        }

        private processMouseOutRegionView(regionView: RegionView) {

            // Remove over class from region
            regionView.refreshEmptyState().removeClass(this.DRAGGED_OVER_CLASS);
        }


        createSortable(selector): void {

            wemjq(selector).sortable({
                // append helper to pageView so it doesn't jump when sortable jumps
                // because of adding/removing placeholder (appended to sortable by default)
                // Don't use api.dom.Body.get() because it may return the parent's body if it had been called there earlier
                appendTo: document.body,
                revert: false,
                cancel: this.ITEM_NOT_DRAGGABLE_SELECTOR,
                connectWith: this.REGION_SELECTOR,
                items: this.SORTABLE_ITEMS_SELECTOR,
                distance: 20,
                delay: 50,
                tolerance: 'intersect',
                cursor: 'move',
                cursorAt: DragHelper.CURSOR_AT,
                scrollSensitivity: this.calculateScrollSensitivity(),
                placeholder: this.PLACEHOLDER_CONTAINER_SELECTOR,
                forceHelperSize: true,
                helper: (event, ui) => DragHelper.get().getHTMLElement(),
                start: (event, ui) => this.handleSortStart(event, ui),
                activate: (event, ui) => this.handleActivate(event, ui),
                over: (event, ui) => this.handleDragOver(event, ui),
                out: (event, ui) => this.handleDragOut(event, ui),
                beforeStop: (event, ui) => this.handleBeforeStop(event, ui),
                receive: (event, ui) => this.handleReceive(event, ui),
                deactivate: (event, ui) => this.handleDeactivate(event, ui),
                stop: (event, ui) => this.handleSortStop(event, ui),
                //change: (event, ui) => this.handleSortChange(event, ui),
                //update: (event, ui) => this.handleSortUpdate(event, ui),
                remove: (event, ui) => this.handleRemove(event, ui)
            });
        }

        // Used by the Context Window when dragging above the IFrame
        createDraggable(jq: JQuery) {

            this._newItemItemType = ItemType.fromHTMLElement(jq.get(0));

            jq.draggable({
                connectToSortable: this.REGION_SELECTOR,
                addClasses: false,
                cursor: 'move',
                appendTo: 'body',
                cursorAt: DragHelper.CURSOR_AT,
                helper: (event, ui) => DragHelper.get().getHTMLElement(),
                start: (event, ui) => this.handleDraggableStart(event, ui),
                stop: (event, ui) => this.handleDraggableStop(event, ui)
            });
        }

        // Used by the Context Window when dragging above the IFrame
        destroyDraggable(jq: JQuery) {
            jq.draggable("destroy");
            this._wasDestroyed = true;
        }

        /*
         *  Sortable start is not fired in Firefox when dragging item from context window
         *  So listen for the draggable events as well
         */
        handleDraggableStart(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleDraggableStart");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            this.notifyDragStarted(this._draggedComponentView);
        }

        /*
         *  Sortable stop is not fired in Firefox when dragging item from context window
         *  So listen for the draggable events as well
         */
        handleDraggableStop(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleDraggableStop");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            if (!this._wasDropped) {
                this.notifyCanceled(this._draggedComponentView);
            }

            this.notifyDragStopped(this._draggedComponentView);
        }

        /*
         *  This event is triggered when sorting starts.
         */
        handleSortStart(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleSortStart");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            var regionView: RegionView = this.getRegionView(ui.item.parent());

            var itemType: ItemType;
            var placeholder = DragPlaceholder.get().setRegionView(regionView);

            if (this.isDraggingFromContextWindow()) {
                // Dragging from context window
                itemType = this._newItemItemType;
            } else {
                // Dragging between sortables
                this._draggedComponentView = this.getComponentView(ui.item);

                this._draggedComponentView.deselect();
                this._draggedComponentView.setMoving(true);

                itemType = this._draggedComponentView.getType();
            }

            // Set it as html first time only
            // update the singleton after
            ui.placeholder.append(placeholder.setItemType(itemType).getHTMLElement());
            ui.helper.show();

            this.processMouseOverRegionView(regionView);

            this.updateHelperAndPlaceholder(regionView);

            this.updateScrollSensitivity(event.target);

            this.notifyDragStarted(this._draggedComponentView);
        }

        /*
         * This event is triggered when sorting stops, but when the placeholder/helper is still available.
         */
        handleBeforeStop(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleBeforeStop");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            ui.helper.hide();
        }

        /*
         * This event is triggered when sorting has stopped.
         */
        handleSortStop(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleSortStop");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            if (this._wasDestroyed) {
                this.cancelDrag(<HTMLElement> event.target);
                this._wasDestroyed = false;
                return;
            }

            var regionView: RegionView = this.getRegionView(ui.item.parent());

            if (!DragHelper.get().isDropAllowed()) {
                // Don't allow to drop if it is forbidden (i.e. layout on layout, or outside region)
                if (DragAndDrop.debug) {
                    console.log("DragAndDrop.handleStop: cancelling drag because it is not allowed to drop here...");
                }

                this.cancelDrag(<HTMLElement> event.target);
            } else {
                var componentIndex = wemjq('>.drag-helper, >.' + api.StyleHelper.getCls("item-view"),
                    regionView.getHTMLElement()).index(ui.item);

                if (this.isDraggingFromContextWindow()) {
                    if (this.pageView.isLocked()) {
                        this.pageView.setLocked(false);
                    }
                    // Create component and view if we drag from context window
                    var componentType: ComponentItemType = <ComponentItemType> this._newItemItemType;

                    var newComponent = regionView.createComponent(componentType.toComponentType());

                    this._draggedComponentView = componentType.createView(new CreateItemViewConfig<RegionView,Component>().
                        setParentView(regionView).
                        setParentElement(regionView).
                        setData(newComponent).
                        setPositionIndex(componentIndex));

                    regionView.addComponentView(this._draggedComponentView, componentIndex, true);

                } else {
                    // Move component to other region
                    if (this._draggedComponentView.hasComponentPath()) {
                        this._draggedComponentView.moveToRegion(regionView, componentIndex);
                    }
                }

                this.notifyDropped(this._draggedComponentView, regionView);
            }

            if (!this.isDraggingFromContextWindow()) {
                this._draggedComponentView.setMoving(false);
            }

            regionView.refreshEmptyState();

            this.notifyDragStopped(this._draggedComponentView);

            // Cleanup

            if (this.isDraggingFromContextWindow()) {
                // remove item if dragging from context window
                ui.item.remove();
            }
            this._newItemItemType = null;
            this._draggedComponentView = null;
        }

        /*
         * This event is triggered when using connected lists, every connected list on drag start receives it.
         */
        handleActivate(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleActivate");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            this.getRegionView(wemjq(event.target)).addClass(this.DRAGGING_ACTIVE_CLASS);
        }

        /*
         * This event is triggered when sorting was stopped, is propagated to all possible connected lists.
         */
        handleDeactivate(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleDeactivate");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            this.getRegionView(wemjq(event.target)).removeClass(this.DRAGGING_ACTIVE_CLASS);
        }

        /*
         *  This event is triggered when a sortable item is moved into a sortable list.
         */
        handleDragOver(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleDragOver");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            var regionView: RegionView = this.getRegionView(ui.placeholder.parent());

            this.processMouseOverRegionView(regionView);

            this.updateHelperAndPlaceholder(regionView);
        }

        /*
         *  This event is triggered when a sortable item is moved away from a sortable list.
         */
        handleDragOut(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleDragOut");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            if (!ui.placeholder || !ui.placeholder.parent()[0]) {
                if (DragAndDrop.debug) {
                    console.log('DragAndDrop.handleDragOut skipping because there is no placeholder, probably item has been already dropped...');
                }
                return;
            }

            var regionView: RegionView = this.getRegionView(ui.placeholder.parent());

            this.processMouseOutRegionView(regionView);

            this.updateHelperAndPlaceholder(regionView, false);
        }

        /*
         *  This event is triggered during sorting, but only when the DOM position has changed.
         */
        handleSortChange(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleSortChange");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

        }

        /*
         *  This event is triggered when the user stopped sorting and the DOM position has changed.
         */
        handleSortUpdate(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleSortUpdate");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

        }

        /*
         * This event is triggered when a sortable item from the list has been dropped into another. The former is the event target.
         */
        handleRemove(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleRemove");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            var fromRegionView = this.getRegionView(wemjq(event.target));
            fromRegionView.refreshEmptyState();
        }

        /*
         *  This event is triggered when an item from a connected sortable list has been dropped into another list. The latter is the event target.
         */
        handleReceive(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {
            if (DragAndDrop.debug) {
                console.groupCollapsed((DragAndDrop._messageCounter++) + " DragDropSort.handleReceive");
                console.log("Event", event, "\nUI", ui);
                console.groupEnd();
            }

            var toRegionView = this.getRegionView(wemjq(event.target));
            toRegionView.refreshEmptyState();
        }


        private cancelDrag(sortable: HTMLElement) {

            wemjq(sortable).sortable('cancel');

            this.notifyCanceled(this._draggedComponentView);
        }


        onDragStarted(listener: (componentView: ComponentView<Component>) => void) {
            this.dragStartedListeners.push(listener);
        }

        unDragStarted(listener: (componentView: ComponentView<Component>) => void) {
            this.dragStartedListeners = this.dragStartedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        private notifyDragStarted(componentView: ComponentView<Component>) {
            if (DragAndDrop.debug) {
                console.log('DragAndDrop.notifyDragStarted', componentView);
            }

            this._isDragging = true;
            this._wasDropped = false;
            this.dragStartedListeners.forEach((curr) => {
                curr(componentView);
            });

            new ComponentViewDragStartedEvent(componentView).fire();
        }

        onDragStopped(listener: (componentView: ComponentView<Component>) => void) {
            this.dragStoppedListeners.push(listener);
        }

        unDragStopped(listener: (componentView: ComponentView<Component>) => void) {
            this.dragStoppedListeners = this.dragStoppedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        private notifyDragStopped(componentView: ComponentView<Component>) {
            if (DragAndDrop.debug) {
                console.log('DragAndDrop.notifyDragStopped', componentView);
            }

            this._isDragging = false;
            DragHelper.get().reset();
            this.dragStoppedListeners.forEach((curr) => {
                curr(componentView);
            });

            new ComponentViewDragStoppedEvent(componentView).fire();
        }

        onDropped(listener: (componentView: ComponentView<Component>, regionView: RegionView) => void) {
            this.droppedListeners.push(listener);
        }

        unDropped(listener: (componentView: ComponentView<Component>, regionView: RegionView) => void) {
            this.droppedListeners = this.droppedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        private notifyDropped(componentView: ComponentView<Component>, regionView: RegionView) {
            if (DragAndDrop.debug) {
                console.log('DragAndDrop.notifyDropped', componentView, regionView);
            }

            this.droppedListeners.forEach((curr) => {
                curr(componentView, regionView);
            });

            this._wasDropped = true;
            new ComponentViewDragDroppedEvent(componentView, regionView).fire();
        }

        onCanceled(listener: (componentView: ComponentView<Component>) => void) {
            this.canceledListeners.push(listener);
        }

        unCanceled(listener: (componentView: ComponentView<Component>) => void) {
            this.canceledListeners = this.canceledListeners.filter((curr) => {
                return curr != listener;
            })
        }

        private notifyCanceled(componentView: ComponentView<Component>) {
            if (DragAndDrop.debug) {
                console.log('DragAndDrop.notifyCanceled', componentView);
            }

            this.canceledListeners.forEach((curr) => {
                curr(componentView);
            });

            new ComponentViewDragCanceledEvent(componentView).fire();
        }


        private updateHelperAndPlaceholder(regionView: RegionView, enter: boolean = true) {
            var helper = DragHelper.get();
            var placeholder = DragPlaceholder.get().setRegionView(enter ? regionView : null);

            helper.setItemName(this._draggedComponentView ?
                               this._draggedComponentView.getName() : api.util.StringHelper.capitalize(this.getItemType().getShortName()));

            if (!enter) {
                helper.setDropAllowed(false);
            } else if (this.isDraggingLayoutOverLayout(regionView, this.getItemType())) {
                helper.setDropAllowed(false);
                placeholder.setText("Layout within layout not allowed");
                placeholder.setDropAllowed(false);
            } else {
                helper.setDropAllowed(true);
            }
        }


        private getItemType(): ItemType {
            if (this._draggedComponentView) {
                return this._draggedComponentView.getType();
            } else if (this._newItemItemType) {
                return this._newItemItemType;
            } else {
                throw new Error('Dragged component and new item type can not be both null');
            }
        }


        private isDraggingFromContextWindow(): boolean {
            return !!this._newItemItemType;
        }


        private isDraggingLayoutOverLayout(regionView: RegionView, draggingItemType: ItemType): boolean {
            var isLayout = regionView.hasParentLayoutComponentView() && draggingItemType.getShortName() == 'layout';
            if (!isLayout) {
                var itemType = this.getItemType();
                if (api.liveedit.fragment.FragmentItemType.get().equals(itemType)) {
                    var fragment = <api.liveedit.fragment.FragmentComponentView> this._draggedComponentView;
                    isLayout = fragment && fragment.containsLayout();
                    if (isLayout && DragAndDrop.debug) {
                        console.log('DragAndDrop.isDraggingLayoutOverLayout - Fragment contains layout');
                    }
                }
            }
            if (DragAndDrop.debug) {
                console.log('DragAndDrop.isDraggingLayoutOverLayout = ' + isLayout);
            }
            return isLayout;
        }


        private getComponentView(jq: JQuery): ComponentView<Component> {
            var comp = this.pageView.getComponentViewByElement(jq.get(0));
            api.util.assertState(!!comp, "ComponentView is not expected to be null");
            return comp;
        }


        private getRegionView(jq: JQuery): RegionView {
            var region = this.pageView.getRegionViewByElement(jq.get(0));
            api.util.assertState(!!region, "RegionView is not expected to be null");
            return region;
        }

        private createSortableItemsSelector(): string {

            var sortableItemsSelector: string[] = [];
            ItemType.getDraggables().forEach((draggableItemType: ItemType) => {
                sortableItemsSelector.push(draggableItemType.getConfig().getCssSelector());
            });

            return sortableItemsSelector.toString();
        }

        private updateScrollSensitivity(selector): void {
            var scrollSensitivity = this.calculateScrollSensitivity();
            wemjq(selector).sortable('option', 'scrollSensitivity', scrollSensitivity);
        }

        private calculateScrollSensitivity(): number {
            // use getViewPortSize() instead of document.body.clientHeight which returned the height of the whole rendered page, not just of the part visible in LiveEdit
            var height = wemjq(window).height();
            var scrollSensitivity = Math.round(height / 8);
            scrollSensitivity = Math.max(20, Math.min(scrollSensitivity, 100));
            return scrollSensitivity
        }

    }
}