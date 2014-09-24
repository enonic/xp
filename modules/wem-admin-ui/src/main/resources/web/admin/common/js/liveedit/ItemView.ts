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

        contextMenuActions: api.ui.Action[];

        contextMenuTitle: ItemViewContextMenuTitle;

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

        setContextMenuActions(actions: api.ui.Action[]): ItemViewBuilder {
            this.contextMenuActions = actions;
            return this;
        }

        setContextMenuTitle(title: ItemViewContextMenuTitle): ItemViewBuilder {
            this.contextMenuTitle = title;
            return this;
        }
    }

    export class ItemView extends api.dom.Element {

        private itemViewIdProducer: ItemViewIdProducer;

        private type: ItemType;

        private parentItemView: ItemView;

        private loadMask: api.ui.mask.LoadMask;

        private tooltip: api.ui.Tooltip;

        private contextMenu: api.liveedit.ItemViewContextMenu;

        private tooltipViewer: api.ui.Viewer<any>;

        private mouseOver: boolean;

        private mouseOverViewListeners: {(): void} [];

        private mouseOutViewListeners: {(): void} [];

        private contextMenuActions: api.ui.Action[];

        private contextMenuTitle: ItemViewContextMenuTitle;

        private debug: boolean;

        constructor(builder: ItemViewBuilder) {
            api.util.assertNotNull(builder.type, "type cannot be null");

            this.debug = false;
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


            this.tooltipViewer = this.getTooltipViewer();
            if (this.tooltipViewer) {
                this.tooltip = new api.ui.Tooltip(this).
                    setSide(api.ui.Tooltip.SIDE_BOTTOM).
                    setMode(api.ui.Tooltip.MODE_FOLLOW).
                    setTrigger(api.ui.Tooltip.TRIGGER_NONE).
                    setHideTimeout(0).
                    setContent(this.tooltipViewer);
            }

            this.contextMenuActions = builder.contextMenuActions;
            this.contextMenuTitle = builder.contextMenuTitle;

            this.onMouseEnter(this.handleMouseEnter.bind(this));
            this.onMouseLeave(this.handleMouseLeave.bind(this));
            this.onClicked(this.handleClick.bind(this));
            this.onContextMenu(this.handleClick.bind(this));
            this.onTouchStart(this.handleClick.bind(this));

        }

        remove() {
            if (this.contextMenu) {
                this.contextMenu.remove();
            }
            if (this.loadMask) {
                this.loadMask.remove();
            }
            super.remove();
        }

        scrollComponentIntoView(): void {
            var dimensions = this.getElementDimensions();
            var screenTopPosition: number = document.body.scrollTop != 0 ? document.body.scrollTop : document.documentElement.scrollTop;
            if (dimensions.top != undefined && dimensions.top - 10 < screenTopPosition) {
                wemjq("html,body").animate({scrollTop: dimensions.top - 10}, 200);
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

            var className = api.util.getClassName(this);
            if (this.debug) {
                console.info("mouse enter start --> ", className);
            }

            if (this.mouseOver) {
                if (this.debug) {
                    console.log('   mouseOver = true, returning', className);
                }
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
                var viewName = api.util.getClassName(view);
                if (view.mouseOver) {
                    if (this.debug) {
                        console.log('   notifying parent mouse out', viewName);
                    }
                    view.notifyMouseOutView();
                } else {
                    view.mouseOver = true;
                    if (this.debug) {
                        console.log('   setting parent mouseOver = true', viewName);
                    }
                    view.notifyMouseOverView();
                    view.notifyMouseOutView();
                }
            });

            // Turn on 'mouseOver' state for this element and notify it entered.
            this.mouseOver = true;
            if (this.debug) {
                console.log('   notifying target mouse over', className);
            }
            this.notifyMouseOverView();

            if (this.debug) {
                console.info("mouse enter end --> ", className);
            }
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

            var className = api.util.getClassName(this);
            if (this.debug) {
                console.info("mouse leave start <-- ", className);
            }

            // Turn off 'mouseOver' state and notify ItemVeiw was left.
            this.mouseOver = false;
            if (this.debug) {
                console.log('   notifying target mouse out', className);
            }
            this.notifyMouseOutView();
            this.tooltip.hide();

            // Notify parent ItemView is entered.
            if (this.parentItemView) {
                if (this.debug) {
                    console.log('   notifying parent mouse over', className);
                }
                this.parentItemView.notifyMouseOverView();
            }

            if (this.debug) {
                console.info("mouse leave end <-- ", className);
            }
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            if (!this.isSelected()) {
                // we prevented mouse events to bubble up so if parent view is selected
                // it won't receive mouse event and won't be deselected
                // therefore we deselect it manually
                this.deselectParent();

                this.select(!this.isEmpty() ? { x: event.pageX, y: event.pageY } : null);
            } else {
                this.deselect();
            }
        }

        deselectParent() {
            for (var parent = this.parentItemView; parent; parent = parent.parentItemView) {
                if (parent.isSelected()) {
                    parent.deselect();
                    return;
                }
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

        hideTooltip(hideParentTooltip: boolean = true) {
            this.tooltip.hide();
            if (hideParentTooltip && this.parentItemView) {
                this.parentItemView.hideTooltip();
            }
        }

        showContextMenu(position?: Position) {
            if (!this.contextMenu) {
                this.contextMenu = new api.liveedit.ItemViewContextMenu(this.contextMenuTitle, this.contextMenuActions);
            }
            var dimensions = this.getElementDimensions();
            var x, y;

            if (position) {
                // show menu at position
                x = position.x;
                y = position.y;
            } else {
                // show menu below if empty or on top
                x = dimensions.left + dimensions.width / 2;
                y = dimensions.top + (this.isEmpty() ? dimensions.height : 0);
            }
            this.contextMenu.showAt(x, y);
        }

        hideContextMenu() {
            if (this.contextMenu) {
                this.contextMenu.hide();
            }
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
            if (api.util.StringHelper.isEmpty(attribute)) {
                return null;
            }
            return ItemViewId.fromString(attribute);
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

        removeEmptyMark() {
            this.getEl().removeAttribute('data-live-edit-empty-component');
            this.removeClass('live-edit-empty-component');
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
            if (!this.loadMask) {
                this.loadMask = new api.ui.mask.LoadMask(this);
                this.appendChild(this.loadMask);
            }
            this.loadMask.show();
        }

        hideLoadingSpinner() {
            if (this.loadMask) {
                this.loadMask.hide();
            }
        }

        setElementDimensions(dimensions: ElementDimensions): void {
            this.getEl().setOffset({
                top: dimensions.top,
                left: dimensions.left
            }).setWidthPx(dimensions.width).setHeightPx(dimensions.height);
        }

        getElementDimensions(): ElementDimensions {
            var el = this.getEl(),
                offset = el.getOffset();

            return {
                top: offset.top,
                left: offset.left,
                width: el.getWidthWithBorder(),
                height: el.getHeightWithBorder()
            };
        }

        toItemViewArray(): ItemView[] {

            return [this];
        }

        toString(): string {

            var s = "id = " + this.getItemId() + ", type = '" + this.type.getShortName() + "'";
            return s;
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
            if (this.debug) {
                console.log("       notify mouse over", api.util.getClassName(this));
            }
            this.mouseOverViewListeners.forEach((listener: () => void) => listener());
        }

        onMouseOutView(listener: () => void) {
            this.mouseOutViewListeners.push(listener);
        }

        unMouseOutView(listener: () => void) {
            this.mouseOutViewListeners = this.mouseOutViewListeners.filter((current) => (current != listener));
        }

        private notifyMouseOutView() {
            if (this.debug) {
                console.log("       notify mouse out", api.util.getClassName(this));
            }
            this.mouseOutViewListeners.forEach((listener: () => void) => listener());
        }
    }
}