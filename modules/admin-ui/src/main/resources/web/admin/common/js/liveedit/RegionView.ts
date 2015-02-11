module api.liveedit {

    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.region.RegionPath;
    import Component = api.content.page.region.Component;
    import ComponentPath = api.content.page.region.ComponentPath;

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

        private itemViewAddedListeners: {(event: ItemViewAddedEvent) : void}[];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent) : void}[];

        private itemViewAddedListener: (event: ItemViewAddedEvent) => void;

        private itemViewRemovedListener: (event: ItemViewRemovedEvent) => void;

        public static debug: boolean;

        constructor(builder: RegionViewBuilder) {

            this.componentViews = [];
            this.itemViewAddedListeners = [];
            this.itemViewRemovedListeners = [];
            this.parentView = builder.parentView;
            RegionView.debug = true;

            this.itemViewAddedListener = (event: ItemViewAddedEvent) => this.notifyItemViewAdded(event.getView());
            this.itemViewRemovedListener = (event: ItemViewRemovedEvent) => {

                // Check if removed ItemView is a child, and remove it if so
                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getView(), ComponentView)) {

                    var removedComponentView: ComponentView<Component> = <ComponentView<Component>>event.getView();
                    var childIndex = this.getComponentViewIndex(removedComponentView);
                    if (childIndex > -1) {
                        this.componentViews.splice(childIndex, 1);
                    }
                }
                this.notifyItemViewRemoved(event.getView());
            };

            super(new ItemViewBuilder().
                setItemViewIdProducer(builder.parentView.getItemViewIdProducer()).
                setType(RegionItemType.get()).
                setElement(builder.element).
                setPlaceholder(new RegionPlaceholder(builder.region)).
                setTooltipViewer(new RegionComponentViewer()).
                setParentElement(builder.parentElement).
                setParentView(builder.parentView).
                setContextMenuActions(this.createRegionContextMenuActions()).
                setContextMenuTitle(new RegionViewContextMenuTitle(builder.region)));

            this.addClass('region-view');
            this.setRegion(builder.region);

            this.parseComponentViews();

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
                    parentView.select(null, ItemViewContextMenuPosition.TOP);
                    parentView.scrollComponentIntoView();
                }
            }));
            actions.push(new api.ui.Action('Empty').onExecuted(() => {
                this.deselect();
                this.empty();
            }));
            return actions;
        }

        getParentItemView(): ItemView {
            return this.parentView;
        }

        setParentItemView(itemView: ItemView) {
            super.setParentItemView(itemView);
            this.parentView = itemView;
        }

        setRegion(region: Region) {
            this.region = region;
            if (region) {
                this.setTooltipObject(region);

                region.onComponentAdded(() => this.refreshEmptyState());
                region.onComponentRemoved(() => this.refreshEmptyState());

                var components = region.getComponents();
                var componentViews = this.getComponentViews();

                componentViews.forEach((view: ComponentView<Component>, index: number) => {
                    view.setComponent(components[index]);
                });
            }
            this.refreshEmptyState();
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

        highlight() {
            if (!this.getPageView().isTextEditMode()) {
                super.highlight();
            }
        }

        showCursor() {
            if (!this.getPageView().isTextEditMode()) {
                super.showCursor();
            }
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();

            var pageView = this.getPageView();
            if (pageView.isTextEditMode()) {
                pageView.setTextEditMode(false);
            } else {
                super.handleClick(event);
            }
        }

        select(clickPosition?: Position, menuPosition?: ItemViewContextMenuPosition) {
            super.select(clickPosition, menuPosition);

            new RegionSelectedEvent(this).fire();
        }

        toString() {
            return super.toString() + " : " + this.getRegionPath().toString();
        }

        registerComponentView(componentView: ComponentView<Component>, index: number) {

            if (RegionView.debug) {
                console.log('RegionView.registerComponentView: ' + componentView.toString())
            }

            if (index >= 0) {
                this.componentViews.splice(index, 0, componentView);
            } else {
                this.componentViews.push(componentView);
            }
            componentView.setParentItemView(this);

            this.notifyItemViewAdded(componentView);

            componentView.onItemViewAdded(this.itemViewAddedListener);
            componentView.onItemViewRemoved(this.itemViewRemovedListener);
        }

        unregisterComponentView(componentView: ComponentView<Component>) {

            if (RegionView.debug) {
                console.log('RegionView.unregisterComponentView: ' + componentView.toString())
            }

            var indexToRemove = this.getComponentViewIndex(componentView);
            if (indexToRemove >= 0) {
                this.componentViews.splice(indexToRemove, 1);
                componentView.setParentItemView(null);

                this.notifyItemViewRemoved(componentView);

                componentView.unItemViewAdded(this.itemViewAddedListener);
                componentView.unItemViewRemoved(this.itemViewRemovedListener);
            }
            else {
                throw new Error("Did not find ComponentView to remove: " + componentView.getItemId().toString());
            }
        }

        addComponentView(componentView: ComponentView<Component>, positionIndex: number) {
            this.insertChild(componentView, positionIndex);
            this.registerComponentView(componentView, positionIndex);

            new ComponentAddedEvent(componentView).fire();
        }

        removeComponentView(componentView: ComponentView<Component>) {
            this.removeChild(componentView);
            this.unregisterComponentView(componentView);

            new ComponentRemovedEvent(componentView).fire();
        }

        getComponentViews(): ComponentView<Component>[] {
            return this.componentViews;
        }

        getComponentViewIndex(view: ComponentView<Component>): number {

            return this.componentViews.indexOf(view);
        }

        getComponentViewByIndex(index: number): ComponentView<Component> {

            return this.componentViews[index];
        }

        getComponentViewByPath(path: ComponentPath): ComponentView<Component> {

            var firstLevelOfPath = path.getFirstLevel();

            if (path.numberOfLevels() == 1) {

                return this.componentViews[firstLevelOfPath.getComponentIndex()];
            }

            for (var i = 0; i < this.componentViews.length; i++) {
                var componentView = this.componentViews[i];
                if (api.ObjectHelper.iFrameSafeInstanceOf(componentView, api.liveedit.layout.LayoutComponentView)) {

                    var layoutView = <api.liveedit.layout.LayoutComponentView>componentView;
                    var match = layoutView.getComponentViewByPath(path.removeFirstLevel());
                    if (match) {
                        return match;
                    }
                }
            }

            return null;
        }

        hasParentLayoutComponentView(): boolean {
            return api.ObjectHelper.iFrameSafeInstanceOf(this.parentView, api.liveedit.layout.LayoutComponentView);
        }

        hasOnlyMovingComponentViews(): boolean {
            return this.componentViews.length > 0 && this.componentViews.every((view: ComponentView<Component>)=> {
                    return view.isMoving();
                })
        }

        private hasComponentDropzone(): boolean {

            var foundDropZone = false;
            var child = this.getHTMLElement().firstChild;
            while (child) {

                if (api.ObjectHelper.iFrameSafeInstanceOf(child, HTMLElement)) {
                    var childHtmlElement = new api.dom.ElementHelper(<HTMLElement> child);
                    if (childHtmlElement.hasClass("drag-placeholder") ||
                        childHtmlElement.hasClass("live-edit-drag-placeholder-container")) {
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
            return !this.hasComponentDropzone() && (!this.region || this.region.isEmpty() || this.hasOnlyMovingComponentViews());
        }

        empty() {
            this.componentViews.forEach((componentView: ComponentView<Component>) => {
                this.removeComponentView(componentView);
            });

            this.region.removeComponents();
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

        unItemViewAdded(listener: (event: ItemViewAddedEvent) => void) {
            this.itemViewAddedListeners = this.itemViewAddedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        private notifyItemViewAddedForAll(itemViews: ItemView[]) {
            itemViews.forEach((itemView: ItemView) => {
                this.notifyItemViewAdded(itemView);
            });
        }

        private notifyItemViewAdded(itemView: ItemView) {
            var event = new ItemViewAddedEvent(itemView);
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

        private notifyItemViewRemovedForAll(itemViews: ItemView[]) {
            itemViews.forEach((itemView: ItemView) => {
                this.notifyItemViewRemoved(itemView);
            });
        }

        private notifyItemViewRemoved(itemView: ItemView) {
            var event = new ItemViewRemovedEvent(itemView);
            this.itemViewRemovedListeners.forEach((listener) => {
                listener(event);
            });
        }

        static isRegionViewFromHTMLElement(htmlElement: HTMLElement): boolean {

            var type = htmlElement.getAttribute("data-" + ItemType.ATTRIBUTE_TYPE);
            if (api.util.StringHelper.isBlank(type)) {
                return false;
            }
            return type == "region";
        }

        private parseComponentViews() {

            this.doParseComponentViews();
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
                    if (component) {
                        var componentView = <ComponentView<Component>> itemType.createView(new CreateItemViewConfig().
                            setParentView(this).
                            setData(component).
                            setElement(childElement).
                            setParentElement(parentElement ? parentElement : this));

                        this.registerComponentView(componentView, componentCount);
                    }
                }
                else {
                    this.doParseComponentViews(childElement)
                }
            });
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
    }
}