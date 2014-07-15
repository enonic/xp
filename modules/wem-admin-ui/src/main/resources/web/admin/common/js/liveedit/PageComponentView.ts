module api.liveedit {

    import Content = api.content.Content;
    import PageComponent = api.content.page.PageComponent;
    import ComponentPath = api.content.page.ComponentPath;
    import ComponentName = api.content.page.ComponentName;

    export class PageComponentViewBuilder<PAGE_COMPONENT extends PageComponent> {

        itemViewProducer: ItemViewIdProducer;

        type: PageComponentItemType;

        parentRegionView: RegionView;

        parentElement: api.dom.Element;

        pageComponent: PAGE_COMPONENT;

        element: api.dom.Element;

        positionIndex: number;

        contextMenuActions: api.ui.Action[];

        /**
         * Optional. The ItemViewIdProducer of parentRegionView will be used if not set.
         */
        setItemViewProducer(value: ItemViewIdProducer): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.itemViewProducer = value;
            return this;
        }

        setType(value: PageComponentItemType): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.type = value;
            return this;
        }

        setParentRegionView(value: RegionView): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.parentRegionView = value;
            return this;
        }

        setParentElement(value: api.dom.Element): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.parentElement = value;
            return this;
        }

        setPageComponent(value: PAGE_COMPONENT): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.pageComponent = value;
            return this;
        }

        setElement(value: api.dom.Element): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.element = value;
            return this;
        }

        setPositionIndex(value: number): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.positionIndex = value;
            return this;
        }

        setContextMenuActions(actions: api.ui.Action[]): PageComponentViewBuilder<PAGE_COMPONENT> {
            this.contextMenuActions = actions;
            return this;
        }
    }

    export class PageComponentView<PAGE_COMPONENT extends PageComponent> extends ItemView {

        private parentRegionView: RegionView;

        private pageComponent: PAGE_COMPONENT;

        private moving: boolean;

        private itemViewAddedListeners: {(event: ItemViewAddedEvent) : void}[];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent) : void}[];

        constructor(builder: PageComponentViewBuilder<PAGE_COMPONENT>) {

            this.itemViewAddedListeners = [];
            this.itemViewRemovedListeners = [];
            this.moving = false;

            super(new ItemViewBuilder().
                    setItemViewIdProducer(builder.itemViewProducer
                        ? builder.itemViewProducer
                        : builder.parentRegionView.getItemViewIdProducer()).
                    setType(builder.type).
                    setElement(builder.element).
                    setParentView(builder.parentRegionView).
                    setParentElement(builder.parentElement).
                    setContextMenuActions(this.createPageComponentContextMenuActions(builder.contextMenuActions))
            );

            this.parentRegionView = builder.parentRegionView;
            this.setPageComponent(builder.pageComponent);
            this.parentRegionView.registerPageComponentView(this, builder.positionIndex);

            // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
            //this.setDraggable(true);
            //this.onDragStart(this.handleDragStart2.bind(this));
            //this.onDrag(this.handleDrag.bind(this));
            //this.onDragEnd(this.handleDragEnd.bind(this));
        }

        private createPageComponentContextMenuActions(actions: api.ui.Action[]): api.ui.Action[] {
            var actions = actions || [];
            actions.push(new api.ui.Action("Parent").onExecuted(() => {
                var parentView: ItemView = this.getParentItemView();
                if (parentView) {
                    parentView.select();
                }
            }));
            actions.push(new api.ui.Action("Empty").onExecuted(() => {
                this.displayPlaceholder();
                this.select();

                new PageComponentResetEvent(this).fire();
            }));
            actions.push(new api.ui.Action("Remove").onExecuted(() => {
                this.deselect();
                this.getParentItemView().removePageComponentView(this);

                new PageComponentRemoveEvent(this).fire();
            }));
            actions.push(new api.ui.Action("Duplicate").onExecuted(() => {
                var duplicatedPageComponent = <PAGE_COMPONENT> this.getPageComponent().duplicateComponent();
                var duplicatedView = this.duplicate(duplicatedPageComponent);
                duplicatedView.select();

                new PageComponentDuplicateEvent(this, duplicatedView).fire();
            }));
            return actions;
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragStart2(event: DragEvent) {

            if (event.target === this.getHTMLElement()) {
                event.dataTransfer.effectAllowed = "move";
                //event.dataTransfer.setData('text/plain', 'This text may be dragged');
                console.log("PageComponentView[" + this.getItemId().toNumber() + "].handleDragStart", event, this.getHTMLElement());
                this.hideTooltip();
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDrag(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                console.log("PageComponentView[" + this.getItemId().toNumber() + "].handleDrag", event, this.getHTMLElement());
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragEnd(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                console.log("PageComponentView[" + this.getItemId().toNumber() + "].handleDragEnd", event, this.getHTMLElement());
                //this.hideTooltip();
            }
        }

        getType(): PageComponentItemType {
            return <PageComponentItemType>super.getType();
        }

        setPageComponent(pageComponent: PAGE_COMPONENT) {
            this.pageComponent = pageComponent;
            if (pageComponent) {
                this.setTooltipObject(pageComponent);
            }
        }

        getPageComponent(): PAGE_COMPONENT {
            return this.pageComponent;
        }

        hasComponentPath(): boolean {
            return !this.pageComponent ? false : true;
        }

        getComponentPath(): ComponentPath {

            if (!this.pageComponent) {
                return null;
            }
            return this.pageComponent.getPath();
        }

        getName(): string {
            return this.pageComponent && this.pageComponent.getName() ? this.pageComponent.getName().toString() : null;
        }

        getParentItemView(): RegionView {
            return this.parentRegionView;
        }

        select(clickPosition ?: Position) {
            super.select(clickPosition);
            new PageComponentSelectEvent(this).fire();
        }

        setMoving(value: boolean) {
            this.moving = value;
        }

        isMoving(): boolean {
            return this.moving;
        }

        displayPlaceholder() {

        }

        duplicate(duplicate: PAGE_COMPONENT): PageComponentView<PAGE_COMPONENT> {
            throw new Error("Must be implemented by inheritors");
        }

        replaceWith(replacement: PageComponentView<PageComponent>) {
            super.replaceWith(replacement);
            this.notifyItemViewRemoved(new ItemViewRemovedEvent(this));
            this.notifyItemViewAdded(new ItemViewAddedEvent(replacement));
        }

        moveToRegion(toRegionView: RegionView, precedingComponentView: PageComponentView<PageComponent>) {

            this.moving = false;
            var precedingComponentIndex: number = -1;
            var precedingComponent: PageComponent = null;
            if (precedingComponentView) {
                precedingComponent = precedingComponentView.getPageComponent();
                precedingComponentIndex = precedingComponentView.getParentItemView().getPageComponentViewIndex(precedingComponentView);
            }

            var indexInNewParent = -1;
            if (precedingComponentIndex >= 0) {
                indexInNewParent = precedingComponentIndex + 1;
            }

            this.getPageComponent().setName(this.getPageComponent().getName());

            // Unregister from previous region...
            // View
            this.parentRegionView.unregisterPageComponentView(this);
            // Data
            this.pageComponent.removeFromParent();
            // Element
            this.unregisterFromParentElement();

            // Register with new region...
            // Register Element only, since it's already added in DOM.
            toRegionView.registerChildElement(this);
            // Data
            toRegionView.getRegion().addComponentAfter(this.pageComponent, precedingComponent);
            // View
            toRegionView.registerPageComponentView(this, indexInNewParent);
            this.parentRegionView = toRegionView;
        }

        onItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners.push(listener);
        }

        notifyItemViewAdded(event: ItemViewAddedEvent) {
            this.itemViewAddedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners.push(listener);
        }

        notifyItemViewRemoved(event: ItemViewRemovedEvent) {
            this.itemViewRemovedListeners.forEach((listener) => {
                listener(event);
            });
        }

        static
        findParentRegionViewHTMLElement(htmlElement: HTMLElement): HTMLElement {

            var parentItemView = ItemView.findParentItemViewAsHTMLElement(htmlElement);
            while (!RegionView.isRegionViewFromHTMLElement(parentItemView)) {
                parentItemView = ItemView.findParentItemViewAsHTMLElement(parentItemView);
            }
            return parentItemView;
        }

        static
        findPrecedingComponentItemViewId(htmlElement: HTMLElement): ItemViewId {

            var previousItemView = ItemView.findPreviousItemView(htmlElement);
            if (!previousItemView) {
                return null;
            }

            var asString = previousItemView.getData(ItemViewId.DATA_ATTRIBUTE);
            if (api.util.isStringEmpty(asString)) {
                return null;
            }
            return ItemViewId.fromString(asString);
        }
    }
}