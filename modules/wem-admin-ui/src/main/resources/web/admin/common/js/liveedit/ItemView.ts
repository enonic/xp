module api.liveedit {

    import PageComponent = api.content.page.PageComponent;

    export interface ElementDimensions {
        top: number;
        left: number;
        width: number;
        height: number;
    }

    export class ItemViewBuilder {

        itemViewIdProducer: ItemViewIdProducer;

        type: ItemType;

        element: api.dom.Element;

        parentElement: api.dom.Element;

        parentView: ItemView;

        setItemViewIdProducer(value: ItemViewIdProducer): ItemViewBuilder {
            this.itemViewIdProducer = value;
            return this;
        }

        setType(value: ItemType): ItemViewBuilder {
            this.type = value;
            return this;
        }

        setElement(value: api.dom.Element): ItemViewBuilder {
            this.element = value;
            return this;
        }

        setParentView(value: ItemView): ItemViewBuilder {
            this.parentView = value;
            return this;
        }

        setParentElement(value: api.dom.Element): ItemViewBuilder {
            this.parentElement = value;
            return this;
        }
    }

    export class ItemView extends api.dom.Element {

        private itemViewIdProducer: ItemViewIdProducer;

        private type: ItemType;

        private parentItemView: ItemView;

        private loadMask: api.ui.LoadMask;

        private elementDimensions: ElementDimensions;

        private tooltip: api.ui.Tooltip;

        private contextMenu: api.ui.menu.ContextMenuWithTitle;

        private tooltipViewer: api.ui.Viewer<any>;

        private mouseOver: boolean;

        private mouseOverViewListeners: {(): void} [];

        private mouseOutViewListeners: {(): void} [];

        constructor(builder: ItemViewBuilder) {
            api.util.assertNotNull(builder.type, "type cannot be null");

            this.type = builder.type;
            this.parentItemView = builder.parentView;
            this.itemViewIdProducer = builder.itemViewIdProducer;

            var props: api.dom.ElementBuilder = null;
            if (builder.element) {
                var elementFromElementBuilder = new api.dom.ElementFromElementBuilder();
                elementFromElementBuilder.setElement(builder.element);
                elementFromElementBuilder.setParentElement(builder.parentElement);
                elementFromElementBuilder.setGenerateId(false);
                props = elementFromElementBuilder;
            }
            else {
                var newElementBuilder = new api.dom.NewElementBuilder();
                newElementBuilder.setTagName("div");
                newElementBuilder.setParentElement(builder.parentElement);
                newElementBuilder.setGenerateId(false);
                props = newElementBuilder;
            }
            super(props);

            this.mouseOver = false;
            this.mouseOverViewListeners = [];
            this.mouseOutViewListeners = [];

            this.setItemId(builder.itemViewIdProducer.next());

            if (!builder.element) {
                this.getEl().setData(ItemType.DATA_ATTRIBUTE, builder.type.getShortName());
            }

            this.loadMask = new api.ui.LoadMask(this);
            this.appendChild(this.loadMask);

            this.tooltipViewer = this.getTooltipViewer();
            if (this.tooltipViewer) {
                this.tooltip = new api.ui.Tooltip(this).
                    setSide(api.ui.Tooltip.SIDE_BOTTOM).
                    setMode(api.ui.Tooltip.MODE_FOLLOW).
                    setTrigger(api.ui.Tooltip.TRIGGER_NONE).
                    setHideTimeout(0).
                    setContent(this.tooltipViewer);
            }

            this.contextMenu = new api.ui.menu.ContextMenuWithTitle();
            this.contextMenu.setName(this.type.getShortName());
            this.contextMenu.setIconClass(this.type.getConfig().getIconCls());
            this.contextMenu.onCloseClicked((event: MouseEvent) => {
                this.deselect();
            });

            this.getType().getConfig().getContextMenuConfig().forEach((itemName: string) => {
                this.contextMenu.addAction(this.createAction(itemName));
            });

            this.setElementDimensions(this.getDimensionsFromElement());

            this.onMouseEnter(this.handleMouseEnter.bind(this));
            this.onMouseLeave(this.handleMouseLeave.bind(this));
            this.onClicked(this.handleClick.bind(this));
            this.onContextMenu(this.handleClick.bind(this));
            this.onTouchStart(this.handleClick.bind(this));

        }

        private createAction(name: string): api.ui.Action {
            var handler, displayName;
            switch (name) {
            case "parent":
                displayName = "Parent";
                handler = () => {
                    var parentView: ItemView = this.getParentItemView();
                    if (parentView) {
                        parentView.select();
                    }
                };
                break;
            case "insert":
                displayName = "Insert";
                handler = () => {
                    //TODO
                };
                break;
            case "reset":
                displayName = "Reset";
                handler = () => {
                    //TODO
                };
                break;
            case "clear":
                displayName = "Empty";
                handler = () => {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(this, PageComponentView)) {
                        var selectedPageComponentView = <PageComponentView<PageComponent>> this;
                        selectedPageComponentView.displayPlaceholder();

                        // update selection
                        this.select();

                        new PageComponentResetEvent(selectedPageComponentView).fire();
                    } else {
                        throw new Error("Emptying [" + api.util.getClassName(this) + "] is not supported");
                    }
                };
                break;
            case "clearRegion":
                displayName = "Empty";
                handler = () => {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(this, RegionView)) {
                        var selectedRegionView = <RegionView> this;

                        selectedRegionView.deselect();
                        selectedRegionView.empty();
                    } else {
                        throw new Error("Expected region to empty, got [" + api.util.getClassName(this) + "]");
                    }
                };
                break;
            case "opencontent":
                displayName = "Open in new tab";
                handler = () => {
                    //TODO
                };
                break;
            case "view":
                displayName = "View";
                handler = () => {
                    //TODO
                };
                break;
            case "edit":
                displayName = "Edit";
                handler = () => {
                    //TODO
                };
                break;
            case "remove":
                displayName = "Remove";
                handler = () => {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(this, PageComponentView)) {
                        var selectedPageComponent = <PageComponentView<PageComponent>> this;

                        var regionView = selectedPageComponent.getParentItemView();
                        regionView.removePageComponentView(selectedPageComponent);

                        this.hideContextMenu();

                        new PageComponentRemoveEvent(selectedPageComponent).fire();
                    } else {
                        throw new Error("Removing [" + api.util.getClassName(this) + "] is not supported");
                    }
                };
                break;
            case "duplicate":
                displayName = "Duplicate";
                handler = () => {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(this, PageComponentView)) {
                        var selectedPageComponentView = <PageComponentView<PageComponent>> this;

                        var origin = selectedPageComponentView.getPageComponent();
                        var duplicatedPageComponent = origin.duplicateComponent();
                        var duplicatedView = selectedPageComponentView.duplicate(duplicatedPageComponent);

                        new PageComponentDuplicateEvent(selectedPageComponentView, duplicatedView).fire();

                        duplicatedView.select();
                    } else {
                        throw new Error("Duplicating [" + api.util.getClassName(this) + "] is not supported");
                    }
                };
                break;
            }
            return new api.ui.Action(displayName).onExecuted(handler);
        }

        private scrollComponentIntoView(): void {
            var dimensions = this.getElementDimensions();
            if (dimensions.top <= window.pageYOffset) {
                wemjq('html, body').animate({scrollTop: dimensions.top - 10}, 200);
            }
        }

        /**
         * Process 'mouseenter' event to track when mouse moves between ItemView's.
         * ItemView notifies that mouse is over it if mouse moves from parent or child ItemView to this one.
         *
         * Method manages two cases:
         * 1. 'mouseenter' was triggered on parent ItemView and then it is triggered on its child ItemView.
         *    - parent has 'mouseOver' state set to 'true';
         *    - the ItemView calls parent.notifyMouseOut(), parent is still in 'mouseOver' state;
         *    - the ItemView receive 'mouseOver' state;
         *    - the ItemView notifies about mouseOver event;
         * 2. 'mouseenter' was triggered on child ItemView before it has been triggered on parent ItemView.
         *    (This occurs when child ItemView is adjacent to its parent's edge.)
         *    - direct parent hasn't received 'mouseOver' state yet;
         *    - look up for the first parent ItemView with 'mouseOver' state, it is ItemView the mouse came from;
         *    - the parent with 'mouseOver' state calls notifyMouseOut();
         *    - go to the previous parent, give it 'mouseOver' state, call notifyMouseOver() and notifyMouseOut() events,
         *      repeat until current ItemView reached;
         *    - set 'mouseOver' state to this ItemView;
         *    - notify about mouseOver event for this ItemView;
         *
         * @param event browser MouseEvent
         */
        private handleMouseEnter(event: MouseEvent) {
            // If ItemView has 'mouseOver' state before it has received 'mouseenter' event,
            // then 'mouseenter' event has already occurred on child ItemView
            // and child has already called notifyMouseOver and notifyMouseOut for this ItemView.
            // No need to process this event.
            if (this.mouseOver) {
                return;
            }

            // Look up for the parent ItemView with 'mouseOver' state.
            // It is direct parent for case 1 or some parent up to the PageView for case 2.
            // Parents are stored to the stack to manage their state and triger events for them further.
            var parentsStack = [];
            for (var parent = this.parentItemView; parent; parent = parent.parentItemView) {
                parentsStack.push(parent);
                if (parent.mouseOver) {
                    break;
                }
            }

            // Stack of parents elements contains single parent element for case 1 or
            // all parents with state 'mouseOver' set to 'false' and first one with 'mouseOver' state.
            // If parent has 'mouseOver' state, notify that mouse is moved out this parent.
            // If parent isn't in 'mouseOver' state, turn it on and notify the parent was entered and left.
            parentsStack.reverse().forEach((view: ItemView) => {
                if (view.mouseOver) {
                    view.notifyMouseOutView();
                } else {
                    view.mouseOver = true;
                    view.notifyMouseOverView();
                    view.notifyMouseOutView();
                }
            });

            // Turn on 'mouseOver' state for this element and notify it entered.
            this.mouseOver = true;
            this.notifyMouseOverView();
        }

        /**
         * Process 'mouseleave' event to track when mouse moves between ItemView's.
         * ItemView notifies that mouse left it when mouse moves to its parent or child ItemView.
         *
         * 'mouseleave' event is always triggered on child element before it has been triggered on parent.
         *
         * @param event browser MouseEvent
         */
        private handleMouseLeave(event: MouseEvent) {
            // Turn of 'mouseOver' state and notify ItemVeiw was left.
            this.mouseOver = false;
            this.notifyMouseOutView();

            // Notify parent ItemView is entered.
            if (this.parentItemView) {
                this.parentItemView.notifyMouseOverView();
            }
        }

        private handleClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            if (!this.isSelected()) {
                this.select(!this.isEmpty() ? { x: event.pageX, y: event.pageY } : null);
            }
        }

        getItemViewIdProducer(): ItemViewIdProducer {
            return this.itemViewIdProducer;
        }

        getTooltipViewer(): api.ui.Viewer<any> {
            // override to render tooltip
            return undefined;
        }

        setTooltipObject(object: any) {
            if (this.tooltipViewer) {
                this.tooltipViewer.setObject(object);
            }
        }

        showTooltip() {
            this.tooltip.show();
        }

        hideTooltip() {
            this.tooltip.hide();
        }

        showContextMenu(position?: Position) {

            var dimensions = this.getElementDimensions();
            var x, y;

            if (position) {
                // show menu at position
                x = position.x - this.contextMenu.getEl().getWidth() / 2;
                y = position.y;
            } else {
                // show menu below if empty or on top
                x = dimensions.left + dimensions.width / 2 - this.contextMenu.getEl().getWidth() / 2;
                y = dimensions.top + (this.isEmpty() ? dimensions.height : 0);
            }
            this.contextMenu.showAt(x, y);
        }

        hideContextMenu() {
            this.contextMenu.hide();
        }

        private setItemId(value: ItemViewId) {
            this.getEl().setAttribute("data-" + ItemViewId.DATA_ATTRIBUTE, value.toString());
        }

        getItemId(): ItemViewId {
            var asString = this.getEl().getAttribute("data-" + ItemViewId.DATA_ATTRIBUTE);
            if (!asString) {
                return null;
            }
            return ItemViewId.fromString(asString);
        }

        static parseItemId(element: HTMLElement): ItemViewId {
            var attribute = element.getAttribute("data-" + ItemViewId.DATA_ATTRIBUTE);
            if (api.util.isStringEmpty(attribute)) {
                return null;
            }
            return ItemViewId.fromString(attribute);
        }

        getElement(): JQuery {
            return wemjq(this.getHTMLElement());
        }

        getType(): ItemType {
            return this.type;
        }

        getParentItemView(): ItemView {
            throw new Error("Must be implemented by inheritors");
        }

        markAsEmpty() {

            this.getEl().setData('live-edit-empty-component', 'true');
            this.addClass("live-edit-empty-component");
        }

        isEmpty(): boolean {
            return this.getEl().hasAttribute('data-live-edit-empty-component');
        }

        isSelected(): boolean {
            return this.getEl().hasAttribute('data-live-edit-selected');
        }

        select(clickPosition?: Position) {
            this.getEl().setData("live-edit-selected", "true");
            this.hideTooltip();
            this.showContextMenu(clickPosition);
            this.scrollComponentIntoView();

            new ItemViewSelectedEvent(this, clickPosition).fire();
        }

        deselect() {
            this.getEl().removeAttribute("data-live-edit-selected");
            this.hideContextMenu();

            new ItemViewDeselectEvent(this).fire();
        }

        getName(): string {
            return '[No Name]';
        }

        showLoadingSpinner() {
            this.loadMask.show();
        }

        hideLoadingSpinner() {
            this.loadMask.hide();
        }

        setElementDimensions(dimensions: ElementDimensions): void {
            this.elementDimensions = dimensions;
        }

        getElementDimensions(): ElementDimensions {
            // We need to dynamically get the dimension as it can change on eg. browser window resize.
            return this.getDimensionsFromElement();
        }

        toItemViewArray(): ItemView[] {

            return [this];
        }

        toString(): string {

            var s = "id = " + this.getItemId() + ", type = '" + this.type.getShortName() + "'";
            return s;
        }

        private getDimensionsFromElement(): ElementDimensions {
            var cmp: JQuery = this.getElement();
            var offset = cmp.offset();
            var top = offset.top;
            var left = offset.left;
            var width = cmp.outerWidth();
            var height = cmp.outerHeight();

            return {
                top: top,
                left: left,
                width: width,
                height: height
            };
        }

        static findParentItemViewAsHTMLElement(htmlElement: HTMLElement): HTMLElement {

            var parentHTMLElement = htmlElement.parentElement;
            var parseItemId = ItemView.parseItemId(parentHTMLElement);
            while (parseItemId == null) {
                parentHTMLElement = parentHTMLElement.parentElement;
                parseItemId = ItemView.parseItemId(parentHTMLElement);
            }

            return parentHTMLElement;
        }

        static findPreviousItemView(htmlElement: HTMLElement): api.dom.ElementHelper {

            var element = new api.dom.ElementHelper(htmlElement);
            var previous = element.getPrevious();
            while (previous != null && !previous.hasAttribute("data-" + ItemType.DATA_ATTRIBUTE)) {
                previous = previous.getPrevious();
            }
            return previous;
        }

        onMouseOverView(listener: () => void) {
            this.mouseOverViewListeners.push(listener);
        }

        unMouseOverView(listener: () => void) {
            this.mouseOverViewListeners = this.mouseOverViewListeners.filter((current) => (current != listener));
        }

        private notifyMouseOverView() {
            this.mouseOverViewListeners.forEach((listener: () => void) => listener());
        }

        onMouseOutView(listener: () => void) {
            this.mouseOutViewListeners.push(listener);
        }

        unMouseOutView(listener: () => void) {
            this.mouseOutViewListeners = this.mouseOutViewListeners.filter((current) => (current != listener));
        }

        private notifyMouseOutView() {
            this.mouseOutViewListeners.forEach((listener: () => void) => listener());
        }
    }
}