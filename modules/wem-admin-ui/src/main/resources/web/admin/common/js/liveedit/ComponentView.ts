module api.liveedit {

    import Component = api.content.page.region.Component;
    import ComponentPath = api.content.page.region.ComponentPath;
    import ComponentName = api.content.page.region.ComponentName;

    export class ComponentViewBuilder<COMPONENT extends Component> {

        placeholder: ComponentPlaceholder;

        itemViewProducer: ItemViewIdProducer;

        type: ComponentItemType;

        parentRegionView: RegionView;

        parentElement: api.dom.Element;

        component: COMPONENT;

        element: api.dom.Element;

        positionIndex: number;

        contextMenuActions: api.ui.Action[];

        setPlaceholder(value: ComponentPlaceholder): ComponentViewBuilder<COMPONENT> {
            this.placeholder = value;
            return this;
        }

        /**
         * Optional. The ItemViewIdProducer of parentRegionView will be used if not set.
         */
        setItemViewProducer(value: ItemViewIdProducer): ComponentViewBuilder<COMPONENT> {
            this.itemViewProducer = value;
            return this;
        }

        setType(value: ComponentItemType): ComponentViewBuilder<COMPONENT> {
            this.type = value;
            return this;
        }

        setParentRegionView(value: RegionView): ComponentViewBuilder<COMPONENT> {
            this.parentRegionView = value;
            return this;
        }

        setParentElement(value: api.dom.Element): ComponentViewBuilder<COMPONENT> {
            this.parentElement = value;
            return this;
        }

        setComponent(value: COMPONENT): ComponentViewBuilder<COMPONENT> {
            this.component = value;
            return this;
        }

        setElement(value: api.dom.Element): ComponentViewBuilder<COMPONENT> {
            this.element = value;
            return this;
        }

        setPositionIndex(value: number): ComponentViewBuilder<COMPONENT> {
            this.positionIndex = value;
            return this;
        }

        setContextMenuActions(actions: api.ui.Action[]): ComponentViewBuilder<COMPONENT> {
            this.contextMenuActions = actions;
            return this;
        }
    }

    export class ComponentView<COMPONENT extends Component> extends ItemView {

        private placeholder: ComponentPlaceholder;

        private parentRegionView: RegionView;

        private component: COMPONENT;

        private moving: boolean;

        private itemViewAddedListeners: {(event: ItemViewAddedEvent) : void}[];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent) : void}[];

        constructor(builder: ComponentViewBuilder<COMPONENT>) {

            this.itemViewAddedListeners = [];
            this.itemViewRemovedListeners = [];
            this.moving = false;
            this.placeholder = builder.placeholder;

            super(new ItemViewBuilder().
                    setItemViewIdProducer(builder.itemViewProducer
                        ? builder.itemViewProducer
                        : builder.parentRegionView.getItemViewIdProducer()).
                    setType(builder.type).
                    setElement(builder.element).
                    setParentView(builder.parentRegionView).
                    setParentElement(builder.parentElement).
                    setContextMenuActions(this.createComponentContextMenuActions(builder.contextMenuActions)).
                    setContextMenuTitle(new ComponentViewContextMenuTitle(builder.component, builder.type))
            );

            this.parentRegionView = builder.parentRegionView;
            this.setComponent(builder.component);
            this.parentRegionView.registerComponentView(this, builder.positionIndex);

            // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
            //this.setDraggable(true);
            //this.onDragStart(this.handleDragStart2.bind(this));
            //this.onDrag(this.handleDrag.bind(this));
            //this.onDragEnd(this.handleDragEnd.bind(this));
        }

        private createComponentContextMenuActions(actions: api.ui.Action[]): api.ui.Action[] {
            var actions = actions || [];
            actions.push(new api.ui.Action("Parent").onExecuted(() => {
                var parentView: ItemView = this.getParentItemView();
                if (parentView) {
                    this.deselect();
                    parentView.select();
                    parentView.scrollComponentIntoView();
                }
            }));
            actions.push(new api.ui.Action("Empty").onExecuted(() => {
                this.displayPlaceholder();
                this.select();
                this.component.reset();
                new ComponentResetEvent(this).fire();
            }));
            actions.push(new api.ui.Action("Remove").onExecuted(() => {
                this.deselect();
                this.getParentItemView().removeComponentView(this);

                new ComponentRemoveEvent(this).fire();
            }));
            actions.push(new api.ui.Action("Duplicate").onExecuted(() => {
                var duplicatedComponent = <COMPONENT> this.getComponent().duplicateComponent();
                var duplicatedView = this.duplicate(duplicatedComponent);
                this.deselect();
                duplicatedView.handleEmptyState();
                duplicatedView.select();
                duplicatedView.showLoadingSpinner();

                new ComponentDuplicateEvent(this, duplicatedView).fire();
            }));
            return actions;
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragStart2(event: DragEvent) {

            if (event.target === this.getHTMLElement()) {
                event.dataTransfer.effectAllowed = "move";
                //event.dataTransfer.setData('text/plain', 'This text may be dragged');
                console.log("ComponentView[" + this.getItemId().toNumber() + "].handleDragStart", event, this.getHTMLElement());
                this.hideTooltip();
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDrag(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                console.log("ComponentView[" + this.getItemId().toNumber() + "].handleDrag", event, this.getHTMLElement());
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragEnd(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                console.log("ComponentView[" + this.getItemId().toNumber() + "].handleDragEnd", event, this.getHTMLElement());
                //this.hideTooltip();
            }
        }

        getType(): ComponentItemType {
            return <ComponentItemType>super.getType();
        }

        setComponent(component: COMPONENT) {
            this.component = component;
            if (component) {
                this.setTooltipObject(component);
            }
        }

        getComponent(): COMPONENT {
            return this.component;
        }

        hasComponentPath(): boolean {
            return !this.component ? false : true;
        }

        getComponentPath(): ComponentPath {

            if (!this.component) {
                return null;
            }
            return this.component.getPath();
        }

        getName(): string {
            return this.component && this.component.getName() ? this.component.getName().toString() : null;
        }

        getParentItemView(): RegionView {
            return this.parentRegionView;
        }

        setMoving(value: boolean) {
            this.moving = value;
        }

        isMoving(): boolean {
            return this.moving;
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);
            if (this.isEmpty()) {
                this.selectPlaceholder();
            }
        }

        selectPlaceholder() {
            this.placeholder.select();
        }

        deselect(silent?: boolean) {
            super.deselect(silent);
            if (this.isEmpty()) {
                this.placeholder.deselect();
            }
        }

        handleEmptyState() {
            super.handleEmptyState();

            if (!this.hasClass("live-edit-empty-component")) {
                this.addClass("live-edit-empty-component");
            }
        }

        displayPlaceholder() {
            this.removeChildren();
            this.appendChild(this.placeholder);
            this.handleEmptyState();
        }

        showRenderingError(url: string, errorMessage?: string) {
            this.addClass("error");
            this.placeholder.showRenderingError(url, errorMessage);
        }

        duplicate(duplicate: COMPONENT): ComponentView<COMPONENT> {
            throw new Error("Must be implemented by inheritors");
        }

        replaceWith(replacement: ComponentView<Component>) {
            super.replaceWith(replacement);
            this.notifyItemViewRemoved(new ItemViewRemovedEvent(this));
            this.notifyItemViewAdded(new ItemViewAddedEvent(replacement));
        }

        moveToRegion(toRegionView: RegionView, precedingComponentView: ComponentView<Component>) {

            this.moving = false;
            var precedingComponentIndex: number = -1;
            var precedingComponent: Component = null;
            if (precedingComponentView) {
                precedingComponent = precedingComponentView.getComponent();
                precedingComponentIndex = precedingComponentView.getParentItemView().getComponentViewIndex(precedingComponentView);
            }

            var indexInNewParent = -1;
            if (precedingComponentIndex >= 0) {
                indexInNewParent = precedingComponentIndex + 1;
            }

            this.getComponent().setName(this.getComponent().getName());

            // Unregister from previous region...
            // View
            this.parentRegionView.unregisterComponentView(this);
            // Data
            this.component.removeFromParent();
            // Element
            this.unregisterFromParentElement();

            // Register with new region...
            // Register Element only, since it's already added in DOM.
            toRegionView.registerChildElement(this);
            // Data
            toRegionView.getRegion().addComponentAfter(this.component, precedingComponent);
            // View
            toRegionView.registerComponentView(this, indexInNewParent);
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
            if (api.util.StringHelper.isEmpty(asString)) {
                return null;
            }
            return ItemViewId.fromString(asString);
        }
    }
}