module api.liveedit {

    import Region = api.content.page.region.Region;
    import RegionPath = api.content.page.RegionPath;
    import Component = api.content.page.Component;

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

        private pageComponentViews: ComponentView<Component>[];

        private placeholder: RegionPlaceholder;

        private itemViewAddedListeners: {(event: ItemViewAddedEvent) : void}[];

        private itemViewRemovedListeners: {(event: ItemViewRemovedEvent) : void}[];

        constructor(builder: RegionViewBuilder) {

            this.pageComponentViews = [];
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

                var pageComponents = region.getComponents();
                var pageComponentViews = this.getPageComponentViews();

                pageComponentViews.forEach((view: ComponentView<Component>, index: number) => {
                    var component = pageComponents[index];
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

        registerPageComponentView(pageComponentView: ComponentView<Component>, index: number) {
            if (index >= 0) {
                this.pageComponentViews.splice(index, 0, pageComponentView);
            }
            else {
                this.pageComponentViews.push(pageComponentView);
            }

            this.notifyItemViewAdded(new ItemViewAddedEvent(pageComponentView));

            pageComponentView.onItemViewAdded((event: ItemViewAddedEvent) => {
                this.notifyItemViewAdded(event);
            });
            pageComponentView.onItemViewRemoved((event: ItemViewRemovedEvent) => {

                // Check if removed ItemView is a child, and remove it if so
                if (api.ObjectHelper.iFrameSafeInstanceOf(event.getView(), ComponentView)) {

                    var removedPageComponentView: ComponentView<Component> = <ComponentView<Component>>event.getView();
                    var childIndex = this.getPageComponentViewIndex(removedPageComponentView);
                    if (childIndex > -1) {
                        this.pageComponentViews.splice(childIndex, 1);
                    }
                }
                this.notifyItemViewRemoved(event);
            });
        }

        unregisterPageComponentView(pageComponentView: ComponentView<Component>) {

            var indexToRemove = this.getPageComponentViewIndex(pageComponentView);
            if (indexToRemove >= 0) {
                this.pageComponentViews.splice(indexToRemove, 1);
                if (this.pageComponentViews.length == 0) {
                    this.placeholder.show();
                }
                this.notifyItemViewRemovedForAll(pageComponentView.toItemViewArray());
            }
            else {
                throw new Error("Did not find ComponentView to remove: " + pageComponentView.getItemId().toString());
            }
        }

        addPageComponentView(pageComponentView: ComponentView<Component>, positionIndex: number) {

            this.placeholder.hide();

            pageComponentView.toItemViewArray().forEach((itemView: ItemView) => {
                this.notifyItemViewAdded(new ItemViewAddedEvent(itemView));
            });

            this.insertChild(pageComponentView, positionIndex);
        }

        getPageComponentViews(): ComponentView<Component>[] {
            return this.pageComponentViews;
        }

        getPageComponentViewIndex(view: ComponentView<Component>): number {

            return this.pageComponentViews.indexOf(view);
        }

        removePageComponentView(pageComponentView: ComponentView<Component>) {

            pageComponentView.remove();
            this.unregisterPageComponentView(pageComponentView);
        }

        hasParentLayoutComponentView(): boolean {
            return api.ObjectHelper.iFrameSafeInstanceOf(this.parentView, api.liveedit.layout.LayoutComponentView);
        }

        refreshPlaceholder() {

            if (this.hasPageComponentViewDropZone()) {
                this.placeholder.hide();
            } else if (this.pageComponentViews.length == 0) {
                this.placeholder.show();
            } else {
                if (this.countNonMovingPageComponentViews() == 0) {
                    this.placeholder.show();
                } else {
                    this.placeholder.hide();
                }
            }
        }

        countNonMovingPageComponentViews(): number {
            var count = 0;
            this.pageComponentViews.forEach((view: ComponentView<Component>)=> {
                if (!view.isMoving()) {
                    count++
                }
            });
            return count;
        }

        private hasPageComponentViewDropZone(): boolean {

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

        empty() {
            this.pageComponentViews.forEach((pageComponentView: ComponentView<Component>) => {
                this.removePageComponentView(pageComponentView);
            });

            this.region.removePageComponents();

            this.refreshPlaceholder();
        }

        toItemViewArray(): ItemView[] {

            var array: ItemView[] = [];
            array.push(this);
            this.pageComponentViews.forEach((pageComponentView: ComponentView<Component>) => {
                var itemViews = pageComponentView.toItemViewArray();
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

        parsePageComponentViews() {

            this.doParsePageComponentViews();
            this.refreshPlaceholder();
        }

        private doParsePageComponentViews(parentElement?: api.dom.Element) {

            var children = parentElement ? parentElement.getChildren() : this.getChildren();
            var region = this.getRegion();
            var pageComponentCount = 0;
            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    api.util.assert(itemType.isPageComponentType(),
                            "Expected ItemView beneath a Region to be a Component: " + itemType.getShortName());

                    var component = region.getComponentByIndex(pageComponentCount++);
                    itemType.createView(new CreateItemViewConfig().
                        setParentView(this).
                        setData(component).
                        setElement(childElement).
                        setParentElement(parentElement ? parentElement : this));
                }
                else {
                    this.doParsePageComponentViews(childElement)
                }
            });
        }
    }
}