/*
 This code contains a lot of prototype coding at the moment.
 A clean up should be done when Live Edit is specked
 */


module LiveEdit.component.dragdropsort.DragDropSort {

    import PageComponent = api.content.page.PageComponent;
    import ComponentName = api.content.page.ComponentName;
    import ItemViewId = api.liveedit.ItemViewId;
    import ItemType = api.liveedit.ItemType;
    import ItemView = api.liveedit.ItemView;
    import PageView = api.liveedit.PageView;
    import RegionView = api.liveedit.RegionView;
    import PageComponentView = api.liveedit.PageComponentView;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import PageComponentItemType = api.liveedit.PageComponentItemType;
    import RegionItemType = api.liveedit.RegionItemType;
    import TextItemType = api.liveedit.text.TextItemType;
    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import DraggingPageComponentViewStartedEvent = api.liveedit.DraggingPageComponentViewStartedEvent;
    import DraggingPageComponentViewCompletedEvent = api.liveedit.DraggingPageComponentViewCompletedEvent;
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
            //console.log("Creating jquerysortable for", element);
            createJQueryUiSortable(wemjq(element));
        });
    }

    function createJQueryUiSortable(selector): void {

        wemjq(selector).sortable({
            revert: false,
            connectWith: REGION_SELECTOR, //removing this solves the over event not firing bug, not sure what it might break though. it broke dragging out of layouts, now seems to work.
            items: SORTABLE_ITEMS_SELECTOR,
            distance: 20,
            delay: 50,
            tolerance: 'intersect',
            cursor: 'move',
            cursorAt: CURSOR_AT,
            scrollSensitivity: calculateScrollSensitivity(),
            placeholder: 'live-edit-drop-target-placeholder',
            zIndex: 1001001,
            helper: (event, helper) => LiveEdit.component.helper.DragHelper.createDragHelperHtml(),
            start: (event, ui) => handleSortStart(event, ui),
            over: (event, ui) => handleDragOver(event, ui),
            out: (event, ui) => handleDragOut(event, ui),
            change: (event, ui) => handleSortChange(event, ui),
            receive: (event, ui) => handleReceive(event, ui),
            update: (event, ui) => handleSortUpdate(event, ui),
            stop: (event, ui) => handleSortStop(event, ui)
        });
    }

    // Used by the Context Window when dragging above the IFrame
    export function createJQueryUiDraggable(contextWindowItem: JQuery): void {
        contextWindowItem.draggable({
            connectToSortable: REGION_SELECTOR,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            zIndex: 5100000,
            cursorAt: CURSOR_AT,
            helper: LiveEdit.component.helper.DragHelper.createDragHelperHtml
        });
    }

    function handleSortStart(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log("DragDropSort.handleSortStart");

        updateScrollSensitivity(event.target);

        var draggedPageComponentView = getPageComponentView(ui.item);

        if (!draggedPageComponentView) {

            var draggingOverRegionView: RegionView = getRegionView(ui.placeholder.parent());
            if (!draggingOverRegionView) {
                console.debug("DragDropSort.handleSortStart: skipping handling since RegionView from ui.placeholder.parent() was not found");
                return;
            }

            ui.placeholder.html(draggingOverRegionView.createPlaceholderForJQuerySortable());
            LiveEdit.component.helper.DragHelper.updateStatusIcon(true);

            // TODO: Not sure what's best, refreshing placeholder for just draggingOverRegionView or all it's children
            // PageView.refreshRegionViewPlaceholdersOfSelfAndSiblings(draggingOverRegionView);
            draggingOverRegionView.refreshPlaceholder();
        }
        else {
            // Mark dragged PageComponentView as "moving"
            draggedPageComponentView.handleDragStart();

            var parentRegionOfDraggedComponent = draggedPageComponentView.getParentItemView();
            parentRegionOfDraggedComponent.hidePlaceholder();
            LiveEdit.component.helper.DragHelper.updateStatusIcon(true);

            ui.placeholder.html(parentRegionOfDraggedComponent.createPlaceholderForJQuerySortable(draggedPageComponentView));

            refreshSortable();
        }

        _isDragging = true;

        new DraggingPageComponentViewStartedEvent().fire();
    }

    function handleDragOver(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log("DragDropSort.handleDragOver");

        var draggingOverRegionView: RegionView = getRegionView(ui.placeholder.parent());
        if (!draggingOverRegionView) {
            console.debug("DragDropSort.handleDragOver: skipping handling since RegionView from ui.placeholder.parent() was not found");
            return;
        }

        var draggedPageComponentView = getPageComponentView(ui.item);
        if (!draggedPageComponentView) {
            console.debug("DragDropSort.handleDragOver: skipping handling since PageComponentView from ui.item was not found");
            return
        }

        event.stopPropagation();

        // Hide placeholder of the Region dragging over
        draggingOverRegionView.hidePlaceholder();


        var isDraggingOverLayoutComponent = api.ObjectHelper.iFrameSafeInstanceOf(draggingOverRegionView.getParentItemView(),
            LayoutComponentView);
        var isDraggingLayoutComponent = draggedPageComponentView.getType().equals(LayoutItemType.get());

        if (isDraggingLayoutComponent && isDraggingOverLayoutComponent) {
            LiveEdit.component.helper.DragHelper.updateStatusIcon(false);

            ui.placeholder.hide();
        } else {
            LiveEdit.component.helper.DragHelper.updateStatusIcon(true);
        }

        ui.placeholder.html(draggingOverRegionView.createPlaceholderForJQuerySortable(draggedPageComponentView));
    }

    function handleDragOut(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log("DragDropSort.handleDragOut");

        ui.placeholder.hide();
        LiveEdit.component.helper.DragHelper.updateStatusIcon(false);

        var parentRegionView: RegionView = null;
        var parentAsJQ = ui.placeholder.parent();
        if (parentAsJQ && parentAsJQ.get(0)) {
            parentRegionView = getRegionView(parentAsJQ);
        }
        if (parentRegionView) {
            PageView.refreshRegionViewPlaceholdersOfSelfAndSiblings(parentRegionView);
        }

        // Ignore the out event if the dragged item is no longer moving (i.e. have been dropped)
        var draggedPageComponentView = getPageComponentView(ui.item);
        if (!draggedPageComponentView) {
            return;
        }

        if (!draggedPageComponentView.isMoving()) {
            return;
        }

        if (draggedPageComponentView) {
            if (targetIsPlaceholder(wemjq(event.target))) {
                removePaddingFromParentLayout(draggedPageComponentView);
            }
        }
    }

    function handleSortChange(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log("DragDropSort.handleSortChange");

        var draggingOverRegionView: RegionView = getRegionView(ui.placeholder.parent());
        if (!draggingOverRegionView) {
            console.debug("DragDropSort.handleSortChange: skipping handling since RegionView from ui.placeholder.parent() was not found");
            return;
        }

        var fromRegionView: RegionView = null;
        if (ui.sender) {
            fromRegionView = getRegionView(ui.sender);
        }

        if (fromRegionView) {
            fromRegionView.refreshPlaceholder();
        }

        addPaddingToParentLayout(draggingOverRegionView);
        LiveEdit.component.helper.DragHelper.updateStatusIcon(true);
        ui.placeholder.show(null);
    }

    function handleSortUpdate(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log("DragDropSort.handleSortUpdate");

        if (ui.item.parent().length == 0) {
            console.debug("DragDropSort.handleSortUpdate: skipping handling since ui.item.parent() does not exist");
            return;
        }

        var droppedInRegionView: RegionView = getRegionView(ui.item.parent());
        if (!droppedInRegionView) {
            console.debug("DragDropSort.handleSortUpdate: skipping handling since RegionView from ui.placeholder.parent() was not found");
            return;
        }

        var liveEditPage = LiveEdit.LiveEditPage.get();
        var droppedPageComponentView = getPageComponentView(ui.item);
        if (!droppedPageComponentView) {
            console.warn("DragDropSort.handleSortUpdate:  skipping handling since PageComponentView from ui.item was not found");
            return;
        }

        // Skip moving when PageComponentView is already moved (happens when moving from one sortable/region to another, then one event is fired for each sortable)
        if (!droppedPageComponentView.isMoving()) {
            return;
        }

        event.stopPropagation();

        if (droppedPageComponentView.hasComponentPath()) {
            droppedPageComponentView.handleDragStop();
            var precedingComponentView = resolvePrecedingComponentView(droppedPageComponentView.getHTMLElement());
            var regionHTMLElement = PageComponentView.findParentRegionViewHTMLElement(droppedPageComponentView.getHTMLElement());

            var regionView = liveEditPage.getRegionViewByElement(regionHTMLElement);

            droppedPageComponentView.moveToRegion(regionView, precedingComponentView);
        }
    }

    function handleSortStop(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log("DragDropSort.handleSortStop");

        var pageComponentView = getPageComponentView(ui.item);
        if (!pageComponentView) {
            console.debug("DragDropSort.handleSortStop: skipping handling since PageComponentView from ui.item not found");
            new ItemFromContextWindowDroppedEvent().fire();
            return;
        }
        _isDragging = false;

        removePaddingFromParentLayout(pageComponentView);

        // TODO: Is this to try prevent adding layout within layout?
        var draggedItemIsLayoutComponent = pageComponentView.getType().equals(LayoutItemType.get());
        var targetComponentIsInLayoutComponent = LayoutComponentView.hasParentLayoutComponentView(pageComponentView);
        if (draggedItemIsLayoutComponent && targetComponentIsInLayoutComponent) {
            ui.item.remove();
        }

        pageComponentView.getElement().removeData('live-edit-selected-on-drag-start');
        pageComponentView.select();

        new DraggingPageComponentViewCompletedEvent(pageComponentView).fire();
    }

    // When sortable receives a new item
    function handleReceive(event: JQueryEventObject, ui: JQueryUI.SortableUIParams): void {

        console.log("DragDropSort.handleReceive");

        if (isItemDraggedFromContextWindow(ui.item)) {
            var liveEditPage = LiveEdit.LiveEditPage.get();

            var droppedElement = wemjq(event.target).children(CONTEXT_WINDOW_DRAG_SOURCE_SELECTOR);
            var regionHTMLElement = PageComponentView.findParentRegionViewHTMLElement(droppedElement.get(0));
            var regionView = liveEditPage.getRegionViewByElement(regionHTMLElement);

            var itemType: PageComponentItemType = <PageComponentItemType>ItemType.byShortName(droppedElement.data('live-edit-type'));
            var precedingComponentView = resolvePrecedingComponentView(droppedElement.get(0));
            var newPageComponent = liveEditPage.createComponent(regionView.getRegion(), itemType.toPageComponentType(),
                precedingComponentView);
            var pageComponentIndex = droppedElement.index();
            var newPageComponentView = itemType.createView(new CreateItemViewConfig<RegionView,PageComponent>().
                setParentView(regionView).
                setData(newPageComponent).
                setPositionIndex(pageComponentIndex));

            droppedElement.remove();

            liveEditPage.addPageComponentView(newPageComponentView, regionView, pageComponentIndex);
        }
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

    function createSortableItemsSelector(): string {

        var sortableItemsSelector: string[] = [];
        ItemType.getDraggables().forEach((draggableItemType: ItemType) => {
            sortableItemsSelector.push(draggableItemType.getConfig().getCssSelector());
        });

        return sortableItemsSelector.toString();
    }

    function resolvePrecedingComponentView(pageComponentViewAsHTMLElement: HTMLElement): PageComponentView<PageComponent> {

        var preceodingComponentView: PageComponentView<PageComponent> = null;
        var precedingComponentViewId = PageComponentView.findPrecedingComponentItemViewId(pageComponentViewAsHTMLElement);
        if (precedingComponentViewId) {
            preceodingComponentView =
            <PageComponentView<PageComponent>>LiveEdit.LiveEditPage.get().getByItemId(precedingComponentViewId);
        }
        return preceodingComponentView;
    }

    function addPaddingToParentLayout(itemView: ItemView) {
        var closestParentLayoutComponentView = LayoutComponentView.getClosestParentLayoutComponentView(itemView);
        if (closestParentLayoutComponentView) {
            closestParentLayoutComponentView.addPadding();
        }
    }

    function removePaddingFromParentLayout(itemView: ItemView) {
        var closestParentLayoutComponentView = LayoutComponentView.getClosestParentLayoutComponentView(itemView);
        if (closestParentLayoutComponentView) {
            closestParentLayoutComponentView.removePadding();
        }
    }

    function getPageComponentView(jq: JQuery) {
        return LiveEdit.LiveEditPage.get().getPageComponentViewByElement(jq.get(0));
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

    function targetIsPlaceholder(target: JQuery): Boolean {
        return target.hasClass('live-edit-drop-target-placeholder')
    }

}