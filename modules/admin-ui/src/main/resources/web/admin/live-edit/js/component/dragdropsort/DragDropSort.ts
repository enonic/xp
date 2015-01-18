/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */


module LiveEdit.component.dragdropsort.DragDropSort {

    import Component = api.content.page.region.Component;
    import ComponentName = api.content.page.region.ComponentName;
    import ItemViewId = api.liveedit.ItemViewId;
    import ItemType = api.liveedit.ItemType;
    import ItemView = api.liveedit.ItemView;
    import PageView = api.liveedit.PageView;
    import RegionView = api.liveedit.RegionView;
    import RegionViewDropZone = api.liveedit.RegionViewDropZone;
    import RegionViewDropZoneBuilder = api.liveedit.RegionViewDropZoneBuilder;
    import ComponentView = api.liveedit.ComponentView;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import ComponentItemType = api.liveedit.ComponentItemType;
    import RegionItemType = api.liveedit.RegionItemType;
    import TextItemType = api.liveedit.text.TextItemType;
    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import DraggingComponentViewStartedEvent = api.liveedit.DraggingComponentViewStartedEvent;
    import DraggingComponentViewCompletedEvent = api.liveedit.DraggingComponentViewCompletedEvent;
    import DraggingComponentViewCanceledEvent = api.liveedit.DraggingComponentViewCanceledEvent;
    import ItemFromContextWindowDroppedEvent = api.liveedit.ItemFromContextWindowDroppedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import CreateItemViewConfig = api.liveedit.CreateItemViewConfig;

    // jQuery sortable cursor position form to the drag helper.
    var CURSOR_AT: any = {left: 24, top: 24};

    // Set up selectors for jQuery.sortable configuration.
    var REGION_SELECTOR: string = RegionItemType.get().getConfig().getCssSelector();

    var CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR: string = '[data-context-window-draggable="true"]';

    var SORTABLE_ITEMS_SELECTOR: string = createSortableItemsSelector();

    var _isDragging: boolean = false;

    var _newItemItemType: api.liveedit.ItemType;

    var _messageCounter: number = 0;

    export function init(): void {
        createJQueryUiSortable(REGION_SELECTOR);
        registerGlobalListeners();
    }

    export function isDragging(): boolean {
        return _isDragging;
    }

    function disableDragDrop(): void {
        wemjq(REGION_SELECTOR).sortable('disable');
    }

    export function cancelDragDrop(selector: string) {
        wemjq(REGION_SELECTOR).sortable('option', 'cancel', selector);
    }

    export function createSortableLayout(component: api.liveedit.ItemView) {
        wemjq(component.getHTMLElement()).find(REGION_SELECTOR).each((index, element) => {
            createJQueryUiSortable(wemjq(element));
        });
    }

    function createJQueryUiSortable(selector): void {

        wemjq(selector).sortable({
            revert: false,
            connectWith: REGION_SELECTOR,
            items: SORTABLE_ITEMS_SELECTOR,
            distance: 20,
            delay: 50,
            tolerance: 'intersect',
            cursor: 'move',
            cursorAt: CURSOR_AT,
            scrollSensitivity: calculateScrollSensitivity(),
            placeholder: 'live-edit-drop-target-placeholder',
            helper: (event, helper) => api.ui.DragHelper.getHtml(),
            start: (event, ui) => handleSortStart(event, ui),
            over: (event, ui) => handleDragOver(event, ui),
            out: (event, ui) => handleDragOut(event, ui),
            change: (event, ui) => handleSortChange(event, ui),
            receive: (event, ui) => handleReceive(event, ui),
            update: (event, ui) => handleSortUpdate(event, ui),
            stop: (event, ui) => handleSortStop(event, ui),
            activate: (event, ui) => handleActivate(event, ui),
            deactivate: (event, ui) => handleDeactivate(event, ui),
            remove: (event, ui) => handleRemove(event, ui)
        });
    }

    // Used by the Context Window when dragging above the IFrame
    export function createJQueryUiDraggable(contextWindowItem: JQuery): void {

        _newItemItemType = api.liveedit.ItemType.fromHTMLElement(contextWindowItem.get(0));

        contextWindowItem.draggable({
            connectToSortable: REGION_SELECTOR,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            cursorAt: CURSOR_AT,
            helper: api.ui.DragHelper.getHtml
        });
    }

    function handleSortStart(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleSortStart");

        var draggedComponentView = getComponentView(ui.item);
        var draggingOverRegionView: RegionView = getRegionView(ui.placeholder.parent());
        api.util.assertState(!!draggingOverRegionView, "draggingOverRegionView should not have been null");

        updateScrollSensitivity(event.target);

        if (!draggedComponentView) {
            api.util.assertState(!!_newItemItemType, "_newItemItemType should not have been null");

            var dropZoneBuilder = new RegionViewDropZoneBuilder().
                setRegionView(draggingOverRegionView).
                setItemType(_newItemItemType);

            if (isDraggingLayoutOverLayout(draggingOverRegionView, _newItemItemType)) {
                api.ui.DragHelper.setDropAllowed(false);
                dropZoneBuilder.setText("Layout within layout not allowed");
                dropZoneBuilder.setDropAllowed(false);
            }
            else {
                api.ui.DragHelper.setDropAllowed(true);
                dropZoneBuilder.setDropAllowed(true);
            }
            ui.placeholder.html(dropZoneBuilder.build().toString());

            draggingOverRegionView.refreshPlaceholder();
        }
        else {
            draggedComponentView.hideContextMenu();
            draggedComponentView.setMoving(true);

            var parentRegionOfDraggedComponent = draggedComponentView.getParentItemView();
            parentRegionOfDraggedComponent.refreshPlaceholder();

            var dropZoneBuilder = new RegionViewDropZoneBuilder().
                setRegionView(draggingOverRegionView).
                setComponentView(draggedComponentView);

            if (isDraggingLayoutOverLayout(draggingOverRegionView, draggedComponentView.getType())) {
                api.ui.DragHelper.setDropAllowed(false);
                dropZoneBuilder.setText("Layout within layout not allowed");
                dropZoneBuilder.setDropAllowed(false);
            }
            else {
                api.ui.DragHelper.setDropAllowed(true);
                dropZoneBuilder.setDropAllowed(true);
            }
            ui.placeholder.html(dropZoneBuilder.build().toString());

            //refreshSortable(); // TODO: Is it really needed? Trying without
        }

        _isDragging = true;

        new DraggingComponentViewStartedEvent().fire();
    }

    function handleDragOver(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleDragOver");

        var draggingOverRegionView: RegionView = getRegionView(ui.placeholder.parent());
        api.util.assertState(!!draggingOverRegionView, "draggingOverRegionView not expected to be null");

        var draggedComponentView = getComponentView(ui.item);
        if (!draggedComponentView) {
            return;
        }

        var dropZoneBuilder = new RegionViewDropZoneBuilder().
            setRegionView(draggingOverRegionView).
            setComponentView(draggedComponentView);

        if (isDraggingLayoutOverLayout(draggingOverRegionView, draggedComponentView.getType())) {
            api.ui.DragHelper.setDropAllowed(false);
            dropZoneBuilder.setText("Layout within layout not allowed");
            dropZoneBuilder.setDropAllowed(false);
            api.ui.DragHelper.setDropAllowed(false);
        }
        else {
            dropZoneBuilder.setDropAllowed(true);
            api.ui.DragHelper.setDropAllowed(true);
        }

        ui.placeholder.html(dropZoneBuilder.build().toString());
        draggingOverRegionView.refreshPlaceholder();

        // Hinders drag out event being fired on parental regions
        event.stopPropagation();
    }

    function handleDragOut(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        // NB: Do not update drag helper status icon on drag out event, since it's fired after helper have been moved into another sortable list
        console.log((_messageCounter++) + " DragDropSort.handleDragOut");
        if (ui.placeholder.parent().length == 0) {
            return;
        }

        var draggedOutOfRegionView: RegionView = getRegionView(ui.placeholder.parent());
        api.util.assertState(!!draggedOutOfRegionView, "draggedOutOfRegionView not expected to be null");

        ui.placeholder.hide();
        draggedOutOfRegionView.refreshPlaceholder();
    }

    function handleSortChange(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleSortChange");

        var draggingOverRegionView: RegionView = getRegionView(ui.placeholder.parent());
        api.util.assertState(!!draggingOverRegionView, "draggingOverRegionView not expected to be null");

        if (ui.sender) {
            var fromRegionView = getRegionView(ui.sender);
            if (fromRegionView) {
                fromRegionView.refreshPlaceholder();
            }
        }
        else {
            ui.placeholder.show();
        }
        draggingOverRegionView.refreshPlaceholder();

        var draggedComponentView = getComponentView(ui.item);
        if (draggedComponentView) {
            if (isDraggingLayoutOverLayout(draggingOverRegionView, draggedComponentView.getType())) {
                api.ui.DragHelper.setDropAllowed(false);
            }
            else {
                api.ui.DragHelper.setDropAllowed(true);
            }
        }
    }

    function handleSortUpdate(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleSortUpdate");

        if (ui.item.parent().length == 0) {
            console.debug("DragDropSort.handleSortUpdate: skipping handling since ui.item.parent() does not exist");
            return;
        }

        event.stopPropagation();

        var droppedInRegionView: RegionView = getRegionView(ui.item.parent());
        api.util.assertState(!!droppedInRegionView, "droppedInRegionView not expected to be null");

        var droppedComponentView = getComponentView(ui.item);

        if (!droppedComponentView) {
            if (isDraggingLayoutOverLayout(droppedInRegionView, _newItemItemType)) {
                ui.item.remove();
                new DraggingComponentViewCanceledEvent(droppedComponentView).fire();
                return;
            }
        } else {
            if (isDraggingLayoutOverLayout(droppedInRegionView,  droppedComponentView.getType())) {
                ui.item.remove();
                new DraggingComponentViewCanceledEvent(droppedComponentView).fire();
                return;
            }
        }

        // Skip moving when ComponentView is already moved (happens when moving from one sortable/region to another, then one event is fired for each sortable)
        if (!droppedComponentView.isMoving()) {
            return;
        }

        if (droppedComponentView.hasComponentPath()) {
            var precedingComponentView = resolvePrecedingComponentView(droppedComponentView.getHTMLElement());
            droppedComponentView.moveToRegion(droppedInRegionView, precedingComponentView);
        }

        droppedInRegionView.refreshPlaceholder();
    }

    // When sortable receives a new item
    function handleReceive(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleReceive");

        if (isItemDraggedFromContextWindow(ui.item)) {
            var liveEditPage = LiveEdit.LiveEditPage.get();

            var droppedElement = wemjq(event.target).children(CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR);
            var regionHTMLElement = ComponentView.findParentRegionViewHTMLElement(droppedElement.get(0));
            var regionView = liveEditPage.getRegionViewByElement(regionHTMLElement);
            var itemType: ComponentItemType = <ComponentItemType>ItemType.byShortName(droppedElement.data('live-edit-type'));

            if (isDraggingLayoutOverLayout(regionView, itemType)) {
                regionView.refreshPlaceholder();
                return;
            }

            var precedingComponentView = resolvePrecedingComponentView(droppedElement.get(0));
            var newComponent = liveEditPage.createComponent(regionView.getRegion(), itemType.toComponentType(),
                precedingComponentView);
            var componentIndex = droppedElement.index();
            var newComponentView = itemType.createView(new CreateItemViewConfig<RegionView,Component>().
                setParentView(regionView).
                setData(newComponent).
                setPositionIndex(componentIndex));

            droppedElement.remove();

            liveEditPage.addComponentView(newComponentView, regionView, componentIndex);
        }
    }

    function handleActivate(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleActivate");

    }

    function handleDeactivate(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleDeactivate");

    }

    function handleRemove(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleRemove");

    }

    function handleSortStop(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log((_messageCounter++) + " DragDropSort.handleSortStop");

        _newItemItemType = null;
        _isDragging = false;

        var componentView = getComponentView(ui.item);
        if (!componentView) {
            new ItemFromContextWindowDroppedEvent().fire();
            return;
        }

        var droppedInRegionView: RegionView = getRegionView(ui.item.parent());
        if (isDraggingLayoutOverLayout(droppedInRegionView, componentView.getType())) {
            ui.item.remove();
        }

        componentView.select();

        new DraggingComponentViewCompletedEvent(componentView).fire();
    }

    function isItemDraggedFromContextWindow(item: JQuery): boolean {
        var isDraggedFromContextWindow: boolean = item.data('context-window-draggable');
        return isDraggedFromContextWindow != undefined && isDraggedFromContextWindow == true;
    }

    function registerGlobalListeners(): void {
       ItemViewDeselectEvent.on(() => {
           if (LiveEdit.DomHelper.supportsTouch() && !_isDragging) {
               disableDragDrop();
           }
       });
    }

    function isDraggingLayoutOverLayout(regionView: RegionView, draggingItemType: ItemType): boolean {
        return regionView.hasParentLayoutComponentView() && LayoutItemType.get().equals(draggingItemType);
    }

    function createSortableItemsSelector(): string {

        var sortableItemsSelector: string[] = [];
        ItemType.getDraggables().forEach((draggableItemType: ItemType) => {
            sortableItemsSelector.push(draggableItemType.getConfig().getCssSelector());
        });

        return sortableItemsSelector.toString();
    }

    function resolvePrecedingComponentView(componentViewAsHTMLElement: HTMLElement): ComponentView<Component> {

        var precedingComponentView: ComponentView<Component> = null;
        var precedingComponentViewId = ComponentView.findPrecedingComponentItemViewId(componentViewAsHTMLElement);
        if (precedingComponentViewId) {
            precedingComponentView =
            <ComponentView<Component>>LiveEdit.LiveEditPage.get().getByItemId(precedingComponentViewId);
        }
        return precedingComponentView;
    }

    function getComponentView(jq: JQuery) {
        return LiveEdit.LiveEditPage.get().getComponentViewByElement(jq.get(0));
    }

    function getRegionView(jq: JQuery) {
        return LiveEdit.LiveEditPage.get().getRegionViewByElement(jq.get(0));
    }

    function updateScrollSensitivity(selector): void {
        var scrollSensitivity = calculateScrollSensitivity();
        wemjq(selector).sortable('option', 'scrollSensitivity', scrollSensitivity);
    }

    function calculateScrollSensitivity(): number {
        // use getViewPortSize() instead of document.body.clientHeight which returned the height of the whole rendered page, not just of the part visible in LiveEdit
        var height = LiveEdit.DomHelper.getViewPortSize().height;
        var scrollSensitivity = Math.round(height / 8);
        scrollSensitivity = Math.max(20, Math.min(scrollSensitivity, 100));
        return scrollSensitivity
    }

    function refreshSortable(): void {
        wemjq(REGION_SELECTOR).sortable('refresh');
    }

}