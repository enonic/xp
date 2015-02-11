module api.liveedit {

    import Component = api.content.page.region.Component;
    import ComponentPath = api.content.page.region.ComponentPath;
    import ComponentName = api.content.page.region.ComponentName;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import ComponentResetEvent = api.content.page.region.ComponentResetEvent;

    export class ComponentViewBuilder<COMPONENT extends Component> {

        itemViewProducer: ItemViewIdProducer;

        type: ComponentItemType;

        parentRegionView: RegionView;

        parentElement: api.dom.Element;

        component: COMPONENT;

        element: api.dom.Element;

        positionIndex: number;

        contextMenuActions: api.ui.Action[];

        placeholder: ComponentPlaceholder;

        tooltipViewer: api.ui.Viewer<any>;

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

        setPlaceholder(value: ComponentPlaceholder): ComponentViewBuilder<COMPONENT> {
            this.placeholder = value;
            return this;
        }

        setTooltipViewer(value: api.ui.Viewer<any>): ComponentViewBuilder<COMPONENT> {
            this.tooltipViewer = value;
            return this;
        }
    }

    export class ComponentView<COMPONENT extends Component> extends ItemView implements api.Cloneable {

        private parentRegionView: RegionView;

        private component: COMPONENT;

        private moving: boolean;

        private itemViewAddedListeners: {(event: ItemViewAddedEvent) : void}[];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent) : void}[];

        private propertyChangedListener: (event: ComponentPropertyChangedEvent) => void;

        private resetListener: (event: ComponentResetEvent) => void;

        constructor(builder: ComponentViewBuilder<COMPONENT>) {

            this.itemViewAddedListeners = [];
            this.itemViewRemovedListeners = [];
            this.moving = false;
            this.parentRegionView = builder.parentRegionView;

            super(new ItemViewBuilder().
                    setItemViewIdProducer(builder.itemViewProducer
                        ? builder.itemViewProducer
                        : builder.parentRegionView.getItemViewIdProducer()).
                    setPlaceholder(builder.placeholder).
                    setTooltipViewer(builder.tooltipViewer).
                    setType(builder.type).
                    setElement(builder.element).
                    setParentView(builder.parentRegionView).
                    setParentElement(builder.parentElement).
                    setContextMenuActions(this.createComponentContextMenuActions(builder.contextMenuActions)).
                    setContextMenuTitle(new ComponentViewContextMenuTitle(builder.component, builder.type))
            );

            this.propertyChangedListener = () => this.refreshEmptyState();
            this.resetListener = () => {
                // recreate the component view from scratch
                // if the component has been reset
                this.deselect();
                var clone = this.clone();
                this.replaceWith(clone);
                clone.select();

                new api.liveedit.ComponentResetEvent(clone).fire();
            };

            this.setComponent(builder.component);

            // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
            //this.setDraggable(true);
            //this.onDragStart(this.handleDragStart2.bind(this));
            //this.onDrag(this.handleDrag.bind(this));
            //this.onDragEnd(this.handleDragEnd.bind(this));
        }

        private registerComponentListeners(component: COMPONENT) {
            component.onReset(this.resetListener);
            component.onPropertyChanged(this.propertyChangedListener);
        }

        private unregisterComponentListeners(component: COMPONENT) {
            component.unPropertyChanged(this.propertyChangedListener);
            component.unReset(this.resetListener);
        }

        private createComponentContextMenuActions(actions: api.ui.Action[]): api.ui.Action[] {
            var actions = actions || [];
            actions.push(new api.ui.Action("Parent").onExecuted(() => {
                var parentView: ItemView = this.getParentItemView();
                if (parentView) {
                    this.deselect();
                    parentView.select(null, ItemViewContextMenuPosition.TOP);
                    parentView.scrollComponentIntoView();
                }
            }));
            actions.push(new api.ui.Action("Empty").onExecuted(() => {
                this.component.reset();
            }));
            actions.push(new api.ui.Action("Remove").onExecuted(() => {
                this.deselect();
                this.getParentItemView().removeComponentView(this);
            }));
            actions.push(new api.ui.Action("Duplicate").onExecuted(() => {
                var duplicatedComponent = <COMPONENT> this.getComponent().duplicateComponent();
                var duplicatedView = this.duplicate(duplicatedComponent);
                this.deselect();
                duplicatedView.select();
                duplicatedView.showLoadingSpinner();

                new ComponentDuplicatedEvent(this, duplicatedView).fire();
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
            if (component) {
                if (this.component) {
                    this.unregisterComponentListeners(this.component);
                }
                this.setTooltipObject(component);
                this.registerComponentListeners(component);
            }

            this.component = component;
            this.refreshEmptyState();
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

        setParentItemView(regionView: RegionView) {
            super.setParentItemView(regionView);
            this.parentRegionView = regionView;
        }

        setMoving(value: boolean) {
            this.moving = value;
        }

        isMoving(): boolean {
            return this.moving;
        }

        clone(): ComponentView<Component> {

            var index = this.getParentItemView().getComponentViewIndex(this);

            var clone = this.getType().createView(
                new CreateItemViewConfig<RegionView,Component>().
                    setParentView(this.getParentItemView()).
                    setParentElement(this.getParentElement()).
                    setData(this.getComponent()).
                    setPositionIndex(index));

            return clone;
        }

        toString() {
            var extra = "";
            if (this.hasComponentPath()) {
                extra = " : " + this.getComponentPath().toString();
            }
            return super.toString() + extra;
        }

        duplicate(duplicate: COMPONENT): ComponentView<COMPONENT> {
            throw new Error("Must be implemented by inheritors");
        }

        replaceWith(replacement: ComponentView<Component>) {
            if (ComponentView.debug) {
                console.log('ComponentView.replaceWith', this, replacement);
            }
            super.replaceWith(replacement);

            var index = this.getParentItemView().getComponentViewIndex(this);

            // unbind the old view from the component and bind the new one
            this.unregisterComponentListeners(this.component);

            var parentRegionView = this.parentRegionView;
            this.parentRegionView.unregisterComponentView(this);
            parentRegionView.registerComponentView(replacement, index);
        }

        moveToRegion(toRegionView: RegionView, precedingComponentView: ComponentView<Component>) {
            if (ComponentView.debug) {
                console.log('ComponentView.moveToRegion', this, this.parentRegionView, toRegionView);
            }

            this.moving = false;
            var indexInNewParent = 0;
            var precedingComponent: Component = null;

            if (precedingComponentView) {
                precedingComponent = precedingComponentView.getComponent();
                indexInNewParent = precedingComponent.getIndex() + 1;
            }

            if (this.parentRegionView.getRegionPath().equals(toRegionView.getRegionPath()) &&
                indexInNewParent == this.parentRegionView.getComponentViewIndex(this)) {

                if (ComponentView.debug) {
                    console.debug('Dropped in the same region at the same index, no need to move', this.parentRegionView, toRegionView);
                }
                return;
            }

            // Unregister from previous region...
            // View
            this.parentRegionView.unregisterComponentView(this);
            // Data
            this.component.removeFromParent();
            // DOM
            this.remove();

            // Register with new region...
            // DOM
            toRegionView.insertChild(this, indexInNewParent);
            // Data
            toRegionView.getRegion().addComponentAfter(this.component, precedingComponent);
            // View
            toRegionView.registerComponentView(this, indexInNewParent);
        }

        onItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners.push(listener);
        }

        unItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners = this.itemViewAddedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        notifyItemViewAdded(view: ItemView) {
            var event = new ItemViewAddedEvent(view);
            this.itemViewAddedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners.push(listener);
        }

        unItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners = this.itemViewRemovedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        notifyItemViewRemoved(view: ItemView) {
            var event = new ItemViewRemovedEvent(view);
            this.itemViewRemovedListeners.forEach((listener) => {
                listener(event);
            });
        }


        static findParentRegionViewHTMLElement(htmlElement: HTMLElement): HTMLElement {

            var parentItemView = ItemView.findParentItemViewAsHTMLElement(htmlElement);
            while (!RegionView.isRegionViewFromHTMLElement(parentItemView)) {
                parentItemView = ItemView.findParentItemViewAsHTMLElement(parentItemView);
            }
            return parentItemView;
        }

        static findPrecedingComponentItemViewId(htmlElement: HTMLElement): ItemViewId {

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