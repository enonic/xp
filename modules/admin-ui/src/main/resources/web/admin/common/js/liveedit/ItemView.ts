module api.liveedit {

    export interface ElementDimensions {
        top: number;
        left: number;
        width: number;
        height: number;
    }

    export class ItemViewBuilder {

        liveEditModel: LiveEditModel;

        itemViewIdProducer: ItemViewIdProducer;

        type: ItemType;

        element: api.dom.Element;

        parentElement: api.dom.Element;

        parentView: ItemView;

        contextMenuActions: api.ui.Action[];

        contextMenuTitle: ItemViewContextMenuTitle;

        placeholder: ItemViewPlaceholder;

        tooltipViewer: api.ui.Viewer<any>;

        setLiveEditModel(value: LiveEditModel): ItemViewBuilder {
            this.liveEditModel = value;
            return this;
        }

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

        setPlaceholder(value: ItemViewPlaceholder): ItemViewBuilder {
            this.placeholder = value;
            return this;
        }

        setTooltipViewer(value: api.ui.Viewer<any>): ItemViewBuilder {
            this.tooltipViewer = value;
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

        liveEditModel: LiveEditModel;

        private itemViewIdProducer: ItemViewIdProducer;

        private placeholder: ItemViewPlaceholder;

        private type: ItemType;

        private parentItemView: ItemView;

        private loadMask: api.ui.mask.LoadMask;

        private tooltip: api.ui.Tooltip;

        private contextMenu: api.liveedit.ItemViewContextMenu;

        private contextMenuTitle: ItemViewContextMenuTitle;

        private contextMenuActions: api.ui.Action[];

        private tooltipViewer: api.ui.Viewer<any>;

        private mouseOver: boolean;

        private mouseOverViewListeners: {(): void} [];

        private mouseOutViewListeners: {(): void} [];

        public static debug: boolean = false;

        constructor(builder: ItemViewBuilder) {
            api.util.assertNotNull(builder.type, "type cannot be null");

            this.type = builder.type;
            this.parentItemView = builder.parentView;
            this.liveEditModel = builder.liveEditModel ? builder.liveEditModel : builder.parentView.liveEditModel;
            this.itemViewIdProducer = builder.itemViewIdProducer;
            this.contextMenuActions = builder.contextMenuActions;
            this.contextMenuTitle = builder.contextMenuTitle;

            var props: api.dom.ElementBuilder = null;
            if (builder.element) {
                var elementFromElementBuilder = new api.dom.ElementFromElementBuilder();
                elementFromElementBuilder.setElement(builder.element);
                elementFromElementBuilder.setParentElement(builder.parentElement);
                elementFromElementBuilder.setGenerateId(false);
                props = elementFromElementBuilder;
            } else {
                var newElementBuilder = new api.dom.NewElementBuilder();
                newElementBuilder.setTagName("div");
                newElementBuilder.setParentElement(builder.parentElement);
                newElementBuilder.setGenerateId(false);
                props = newElementBuilder;
            }
            super(props);
            this.addClass("item-view");

            this.setDraggable(true);

            this.mouseOver = false;
            this.mouseOverViewListeners = [];
            this.mouseOutViewListeners = [];

            this.setItemId(builder.itemViewIdProducer.next());

            if (!builder.element) {
                this.getEl().setData(ItemType.ATTRIBUTE_TYPE, builder.type.getShortName());
            }

            /*          To disable all the tooltips
             if (builder.tooltipViewer) {
             this.tooltipViewer = builder.tooltipViewer;

             this.tooltip = new api.ui.Tooltip(this).
             setSide(api.ui.Tooltip.SIDE_BOTTOM).
             setMode(api.ui.Tooltip.MODE_FOLLOW).
             setTrigger(api.ui.Tooltip.TRIGGER_NONE).
             setHideTimeout(0).
             setContent(this.tooltipViewer);
             }*/

            if (builder.placeholder) {
                this.placeholder = builder.placeholder;
                this.appendChild(this.placeholder);
            }

            // safe to use bind here because it's the same object who handles the events
            // and we are not going to unbind them on remove
            this.onMouseEnter(this.handleMouseEnter.bind(this));
            this.onMouseLeave(this.handleMouseLeave.bind(this));
            this.onClicked(this.handleClick.bind(this));
            this.onContextMenu(this.handleClick.bind(this));
            this.onTouchStart(this.handleClick.bind(this));


            this.bindMouseListeners();
        }

        private bindMouseListeners() {
            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                // no need to check if the view manages shader and highlighter here
                // because it is essential to resize them on window resize
                if (this.isSelected()) {
                    this.highlight();
                    this.shade();
                }
            });

            this.onMouseOverView(() => {
                var hasSelectedView = this.getPageView().hasSelectedView();

                if (!this.isManagingTooltip()) {
                    this.showTooltip();
                }
                if (!this.isManagingHighlighter() && !hasSelectedView) {
                    this.highlight();
                }
                if (!this.isManagingCursor()) {
                    this.showCursor();
                }
            });

            this.onMouseLeaveView(() => {
                var hasSelectedView = this.getPageView().hasSelectedView();

                if (!this.isManagingTooltip()) {
                    this.hideTooltip();
                }
                if (!this.isManagingHighlighter() && !hasSelectedView) {
                    this.unhighlight();
                }
                if (!this.isManagingCursor()) {
                    this.resetCursor();
                }
            });

            Shader.get().onClicked((event: MouseEvent) => {
                if (!this.isManagingShader()) {
                    this.handleShaderClick(event);
                }
            });
        }

        highlight() {
            Highlighter.get().highlightItemView(this);
        }

        unhighlight() {
            Highlighter.get().hide()
        }

        shade() {
            Shader.get().shadeItemView(this);
        }

        unshade() {
            Shader.get().hide();
        }

        showCursor() {
            Cursor.get().displayItemViewCursor(this);
        }

        resetCursor() {
            Cursor.get().reset();
        }

        getPageView(): PageView {
            var pageView = this;
            while (!PageItemType.get().equals(pageView.getType())) {
                pageView = pageView.parentItemView;
            }
            return <PageView> pageView;
        }

        /**
         * Tells if the view wants to manage tooltips itself
         * Will get basic mouse over/out behavior by default
         * @returns {boolean}
         */
        isManagingTooltip(): boolean {
            return false;
        }

        /**
         * Tells if the view wants to manage context menu itself
         * Will get basic mouse selected/deselected behavior by default
         * @returns {boolean}
         */
        isManagingContextMenu(): boolean {
            return false;
        }

        /**
         * Tells if the view wants to manage highlighter itself
         * Will get basic mouse over/out behavior by default
         * @returns {boolean}
         */
        isManagingHighlighter(): boolean {
            return false;
        }

        /**
         * Tells if the view wants to manage shader itself
         * Will get basic click selects/deselects behavior by default
         * @returns {boolean}
         */
        isManagingShader(): boolean {
            return false;
        }

        /**
         * Tells if the view wants to manage cursor itself
         * Will get cursor defined in config on mouse over by default
         * @returns {boolean}
         */
        isManagingCursor(): boolean {
            return false;
        }

        remove(): ItemView {
            if (this.contextMenu) {
                this.contextMenu.remove();
            }
            if (this.loadMask) {
                this.loadMask.remove();
            }

            this.unhighlight();
            this.unshade();

            super.remove();
            return this;
        }

        setDraggable(value: boolean) {
            super.setDraggable(value);
            // tells jquery drag n drop to ignore this draggable
            this.toggleClass('not-draggable', !value);
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
        handleMouseEnter(event: MouseEvent) {
            // If ItemView has 'mouseOver' state before it has received 'mouseenter' event,
            // then 'mouseenter' event has already occurred on child ItemView
            // and child has already called notifyMouseOver and notifyMouseOut for this ItemView.
            // No need to process this event.

            if (ItemView.debug) {
                console.group("mouse enter [" + this.getId() + "]");
            }

            if (this.mouseOver) {
                if (ItemView.debug) {
                    console.log('mouseOver = true, returning.');
                    console.groupEnd();
                }
                return;
            }

            this.manageParentsMouseOver();

            // Turn on 'mouseOver' state for this element and notify it entered.
            this.mouseOver = true;
            this.notifyMouseOverView();

            if (ItemView.debug) {
                console.groupEnd()
            }
        }

        private manageParentsMouseOver() {
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
                    if (ItemView.debug) {
                        console.debug('parent.mouseOver = true, notifying mouse out [' + view.getId() + "]");
                    }
                    view.notifyMouseLeaveView();
                } else {
                    view.mouseOver = true;
                    if (ItemView.debug) {
                        console.debug('parent.mouseOver = false, setting to true [' + view.getId() + "]");
                    }
                    view.notifyMouseOverView();
                    view.notifyMouseLeaveView();
                }
            });
        }

        /**
         * Process 'mouseleave' event to track when mouse moves between ItemView's.
         * ItemView notifies that mouse left it when mouse moves to its parent or child ItemView.
         *
         * 'mouseleave' event is always triggered on child element before it has been triggered on parent.
         *
         * @param event browser MouseEvent
         */
        handleMouseLeave(event: MouseEvent) {

            if (ItemView.debug) {
                console.group("mouse leave [" + this.getId() + "]");
            }

            // Turn off 'mouseOver' state and notify ItemVeiw was left.
            this.mouseOver = false;
            this.notifyMouseLeaveView();
            if (this.tooltip) {
                this.tooltip.hide();
            }

            // Notify parent ItemView is entered.
            if (this.parentItemView) {
                this.parentItemView.notifyMouseOverView();
            }

            if (ItemView.debug) {
                console.groupEnd();
            }
        }

        isEmpty(): boolean {
            throw new Error("Must be implemented by inheritors");
        }

        refreshEmptyState() {
            this.toggleClass('empty', this.isEmpty());
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            if (!this.isSelected()) {
                // we prevented mouse events to bubble up so if parent view is selected
                // it won't receive mouse event and won't be deselected
                // therefore we deselect it manually
                this.deselectParent();

                this.select(!this.isEmpty() ? {x: event.pageX, y: event.pageY} : null);
            } else {
                this.deselect();
            }
        }

        handleShaderClick(event: MouseEvent) {
            if (this.isSelected()) {
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

        setTooltipObject(object: any) {
            if (this.tooltipViewer) {
                this.tooltipViewer.setObject(object);
            }
        }

        showTooltip() {
            if (ItemView.debug) {
                console.log('showing tooltip [' + this.getId() + "]");
            }
            if (this.tooltip) {
                this.tooltip.show();
            }
        }

        hideTooltip(hideParentTooltip: boolean = true) {
            if (ItemView.debug) {
                console.log('hiding tooltip [' + this.getId() + "]");
            }
            if (this.tooltip) {
                this.tooltip.hide();
            }
            if (hideParentTooltip && this.parentItemView) {
                this.parentItemView.hideTooltip();
            }
        }

        showContextMenu(clickPosition?: Position, menuPosition?: ItemViewContextMenuPosition) {
            if (!this.contextMenu) {
                this.contextMenu = new api.liveedit.ItemViewContextMenu(this.contextMenuTitle, this.contextMenuActions);
            }
            var dimensions = this.getElementDimensions();
            var x, y;

            if (clickPosition) {
                // show menu at position
                x = clickPosition.x;
                y = clickPosition.y;
            } else {
                // show menu below if empty or on top
                x = dimensions.left + dimensions.width / 2;
                y = dimensions.top + (ItemViewContextMenuPosition.TOP == menuPosition ? 0 : dimensions.height);
            }
            this.contextMenu.showAt(x, y);
        }

        hideContextMenu() {
            if (this.contextMenu) {
                this.contextMenu.hide();
            }
        }

        setContextMenuActions(actions: api.ui.Action[]) {
            this.contextMenuActions = actions;
            if (this.contextMenu) {
                this.contextMenu.setActions(actions);
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

        isSelected(): boolean {
            return this.getEl().hasAttribute('data-live-edit-selected');
        }

        select(clickPosition?: Position, menuPosition?: ItemViewContextMenuPosition) {
            this.getEl().setData("live-edit-selected", "true");

            if (!this.isManagingContextMenu()) {
                this.showContextMenu(clickPosition, menuPosition);
            }
            if (!this.isManagingTooltip()) {
                this.hideTooltip();
            }
            if (!this.isManagingHighlighter()) {
                this.highlight();
            }
            if (!this.isManagingShader()) {
                this.shade();
            }
            if (!this.isManagingCursor()) {
                this.showCursor();
            }

            // selecting anything should exit the text edit mode
            this.stopTextEditMode();

            if (this.isEmpty()) {
                this.selectPlaceholder();
            }

            new ItemViewSelectedEvent(this, clickPosition).fire();
        }

        deselect(silent?: boolean) {
            this.getEl().removeAttribute("data-live-edit-selected");

            if (!this.isManagingContextMenu()) {
                this.hideContextMenu();
            }
            if (!this.isManagingHighlighter()) {
                this.unhighlight();
            }
            if (!this.isManagingShader()) {
                this.unshade();
            }

            if (this.isEmpty()) {
                this.deselectPlaceholder();
            }

            if (!silent) {
                new ItemViewDeselectEvent(this).fire();
            }
        }

        private stopTextEditMode() {
            var pageView = this.getPageView();
            if (pageView.isTextEditMode()) {
                pageView.setTextEditMode(false);
            }
        }

        private selectPlaceholder() {
            if (this.placeholder) {
                this.placeholder.select();
            }
        }

        private deselectPlaceholder() {
            if (this.placeholder) {
                this.placeholder.deselect();
            }
        }

        showRenderingError(url: string, errorMessage?: string) {
            if (this.placeholder) {
                this.addClass("error");
                this.placeholder.showRenderingError(url, errorMessage);
            }
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
            while (previous != null && !previous.hasAttribute("data-" + ItemType.ATTRIBUTE_TYPE)) {
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
            if (ItemView.debug) {
                console.log("notifying mouse over [" + this.getId() + "]");
            }
            this.mouseOverViewListeners.forEach((listener: () => void) => listener());
        }

        onMouseLeaveView(listener: () => void) {
            this.mouseOutViewListeners.push(listener);
        }

        unMouseLeaveView(listener: () => void) {
            this.mouseOutViewListeners = this.mouseOutViewListeners.filter((current) => (current != listener));
        }

        private notifyMouseLeaveView() {
            if (ItemView.debug) {
                console.log("notifying mouse out [" + this.getId() + "]");
            }
            this.mouseOutViewListeners.forEach((listener: () => void) => listener());
        }
    }
}