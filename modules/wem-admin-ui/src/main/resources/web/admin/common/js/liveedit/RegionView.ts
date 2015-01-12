module api.liveedit {

    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.region.RegionPath;
    import Component = api.content.page.region.Component;

    export class RegionViewBuilder {

        liveEditModel: LiveEditModel;

        parentElement: api.dom.Element;

        parentView: ItemView;

        region: Region;

        element: api.dom.Element;

        setLiveEditModel(value: LiveEditModel): RegionViewBuilder {
            this.liveEditModel = value;
            return this;
        }

        setParentElement(value: api.dom.Element): RegionViewBuilder {
            this.parentElement = value;
            return this;
        }

        setParentView(value: ItemView): RegionViewBuilder {
            this.parentView = value;
            return this;
        }

        setRegion(value: Region): RegionViewBuilder {
            this.region = value;
            return this;
        }

        setElement(value: api.dom.Element): RegionViewBuilder {
            this.element = value;
            return this;
        }
    }

    export class RegionView extends ItemView {

        private parentView: ItemView;

        private region: Region;

        private componentViews: ComponentView<Component>[];

        private placeholder: RegionPlaceholder;

        private itemViewAddedListeners: {(event: ItemViewAddedEvent) : void}[];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent) : void}[];

        constructor(builder: RegionViewBuilder) {

            this.componentViews = [];
            this.itemViewAddedListeners = [];
            this.itemViewRemovedListeners = [];

            super(new ItemViewBuilder().
                setItemViewIdProducer(builder.parentView.getItemViewIdProducer()).
                setType(RegionItemType.get()).
                setElement(builder.element).
                setParentElement(builder.parentElement).
                setParentView(builder.parentView).
                setContextMenuActions(this.createRegionContextMenuActions()).
                setContextMenuTitle(new RegionViewContextMenuTitle(builder.region)));

            this.setRegion(builder.region);

            this.parentView = builder.parentView;
            this.placeholder = new RegionPlaceholder(this);
            this.placeholder.hide();
            this.appendChild(this.placeholder);
            this.refreshPlaceholder();

            // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
            //this.onDragOver(this.handleDragOver.bind(this));
            //this.onDragEnter(this.handleDragEnter.bind(this));
            //this.onDragLeave(this.handleDragLeave.bind(this));
            //this.onDrop(this.handleDrop.bind(this));
        }

        private createRegionContextMenuActions() {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Parent').onExecuted(() => {
                var parentView: ItemView = this.getParentItemView();
                if (parentView) {
                    this.deselect();
                    parentView.select();
                    parentView.scrollComponentIntoView();
                }
            }));
            actions.push(new api.ui.Action('Empty').onExecuted(() => {
                this.deselect();
                this.empty();
            }));
            return actions;
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragEnter(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                console.log("ItemView.handleDragEnter", event, this.getHTMLElement());
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragLeave(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                console.log("ItemView.handleDragLeave", event, this.getHTMLElement());
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDragOver(event: DragEvent) {
            //var itemId = ItemView.parseItemId(<HTMLElement>event.target);
            if (event.target === this.getHTMLElement()) {
                console.log("RegionView[" + this.toString() + "].handleDragOver: ", event.target, event.target);
                event.preventDefault();
            }
        }

        // TODO: by task about using HTML5 DnD api (JVS 2014-06-23) - do not remove
        private handleDrop(event: DragEvent) {
            if (event.target === this.getHTMLElement()) {
                //var itemId = ItemView.parseItemId(<HTMLElement>event.target);
                console.log("RegionView[" + this.toString() + "].handleDrop: ", event.target, this.getHTMLElement());

                event.preventDefault();

                var data = event.dataTransfer.getData("Text");
                //event.target.appendChild(document.getElementById(data));
            }
        }

        getParentItemView(): ItemView {
            return this.parentView;
        }

        setRegion(region: Region) {
            this.region = region;
            if (region) {
                this.setTooltipObject(region);

                var components = region.getComponents();
                var componentViews = this.getComponentViews();

                componentViews.forEach((view: ComponentView<Component>, index: number) => {
                    var component = components[index];
                    view.setComponent(component);
                });
            }
        }

        getRegion(): Region {
            return this.region;
        }

        getRegionName(): string {
            return this.getRegionPath() ? this.getRegionPath().getRegionName() : null;
        }

        getRegionPath(): RegionPath {
            return this.region ? this.region.getPath() : null;
        }

        getName(): string {
            return this.getRegionName() ? this.getRegionName().toString() : "[No Name]";
        }

        select(clickPosition?: Position) {
            new RegionSelectEvent(this).fire();
            super.select(clickPosition);
        }

        getTooltipViewer(): api.ui.Viewer<Region> {
            return new RegionComponentViewer();
        }

        registerComponentView(componentView: ComponentView<Component>, index: number) {
            if (index >= 0) {
                this.componentViews.splice(index, 0, componentView);
            }
            else {
                this.componentViews.push(componentView);
            }

            this.notifyItemViewAdded(new ItemViewAddedEvent(componentView));

            componentView.onItemViewAdded((event: ItemViewAddedEvent) => {
                this.notifyItemViewAdded(event);
            });
            componentView.onItemViewRemoved((event: ItemViewRemovedEvent) => {

                // Check if removed ItemView is a child, and remove it if so
                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getView(), ComponentView)) {

                    var removedComponentView: ComponentView<Component> = <ComponentView<Component>>event.getView();
                    var childIndex = this.getComponentViewIndex(removedComponentView);
                    if (childIndex > -1) {
                        this.componentViews.splice(childIndex, 1);
                    }
                }
                this.notifyItemViewRemoved(event);
            });
        }

        unregisterComponentView(componentView: ComponentView<Component>) {

            var indexToRemove = this.getComponentViewIndex(componentView);
            if (indexToRemove >= 0) {
                this.componentViews.splice(indexToRemove, 1);
                if (this.componentViews.length == 0) {
                    this.placeholder.show();
                }
                this.notifyItemViewRemovedForAll(componentView.toItemViewArray());
            }
            else {
                throw new Error("Did not find ComponentView to remove: " + componentView.getItemId().toString());
            }
        }

        addComponentView(componentView: ComponentView<Component>, positionIndex: number) {

            this.placeholder.hide();

            componentView.toItemViewArray().forEach((itemView: ItemView) => {
                this.notifyItemViewAdded(new ItemViewAddedEvent(itemView));
            });

            this.insertChild(componentView, positionIndex);
        }

        getComponentViews(): ComponentView<Component>[] {
            return this.componentViews;
        }

        getComponentViewIndex(view: ComponentView<Component>): number {

            return this.componentViews.indexOf(view);
        }

        removeComponentView(componentView: ComponentView<Component>) {

            componentView.remove();
            this.unregisterComponentView(componentView);
        }

        hasParentLayoutComponentView(): boolean {
            return api.ObjectHelper.iFrameSafeInstanceOf(this.parentView, api.liveedit.layout.LayoutComponentView);
        }

        refreshPlaceholder() {

            if (this.hasComponentViewDropZone()) {
                this.placeholder.hide();
            } else if (this.componentViews.length == 0) {
                this.placeholder.show();
            } else {
                if (this.countNonMovingComponentViews() == 0) {
                    this.placeholder.show();
                } else {
                    this.placeholder.hide();
                }
            }
        }

        countNonMovingComponentViews(): number {
            var count = 0;
            this.componentViews.forEach((view: ComponentView<Component>)=> {
                if (!view.isMoving()) {
                    count++
                }
            });
            return count;
        }

        private hasComponentViewDropZone(): boolean {

            var foundDropZone = false;
            var child = this.getHTMLElement().firstChild;
            while (child) {

                if (api.ObjectHelper.iFrameSafeInstanceOf(child, HTMLElement)) {
                    var childHtmlElement = new api.dom.ElementHelper(<HTMLElement> child);
                    if (childHtmlElement.hasClass("region-view-drop-zone") ||
                        childHtmlElement.hasClass("live-edit-drop-target-placeholder")) {
                        if (childHtmlElement.getDisplay() != "none") {
                            foundDropZone = true;
                            break;
                        }
                    }
                }

                child = child.nextSibling;
            }
            return foundDropZone;
        }

        isEmpty(): boolean {
            return this.region.isEmpty();
        }

        empty() {
            this.componentViews.forEach((componentView: ComponentView<Component>) => {
                this.removeComponentView(componentView);
            });

            this.region.removeComponents();

            this.refreshPlaceholder();
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.componentViews.forEach((componentView: ComponentView<Component>) => {
                var itemViews = componentView.toItemViewArray();
                array = array.concat(itemViews);
            });
            return array;
        }

        onItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners.push(listener);
        }

        private notifyItemViewAdded(event: ItemViewAddedEvent) {
            this.itemViewAddedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onItemViewRemoved(listener: (event: ItemViewRemovedEvent) => void) {
            this.itemViewRemovedListeners.push(listener);
        }

        private notifyItemViewRemovedForAll(itemViews: ItemView[]) {
            itemViews.forEach((curr: ItemView) => {
                this.notifyItemViewRemoved(new ItemViewRemovedEvent(curr));
            });
        }

        private notifyItemViewRemoved(event: ItemViewRemovedEvent) {
            this.itemViewRemovedListeners.forEach((listener) => {
                listener(event);
            });
        }

        static isRegionViewFromHTMLElement(htmlElement: HTMLElement): boolean {

            var type = htmlElement.getAttribute("data-" + ItemType.DATA_ATTRIBUTE);
            if (api.util.StringHelper.isBlank(type)) {
                return false;
            }
            return type == "region";
        }

        parseComponentViews() {

            this.doParseComponentViews();
            this.refreshPlaceholder();
        }

        private doParseComponentViews(parentElement?: api.dom.Element) {

            var children = parentElement ? parentElement.getChildren() : this.getChildren();
            var region = this.getRegion();
            var componentCount = 0;
            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    api.util.assert(itemType.isComponentType(),
                            "Expected ItemView beneath a Region to be a Component: " + itemType.getShortName());

                    var component = region.getComponentByIndex(componentCount++);
                    itemType.createView(new CreateItemViewConfig().
                        setParentView(this).
                        setData(component).
                        setElement(childElement).
                        setParentElement(parentElement ? parentElement : this));
                }
                else {
                    this.doParseComponentViews(childElement)
                }
            });
        }
    }
}