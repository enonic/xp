module api.liveedit {

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

        private tooltipViewer: api.ui.Viewer<any>;

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

            this.setElementDimensions(this.getDimensionsFromElement());
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

            new ItemViewSelectedEvent(this, clickPosition).fire();
        }

        deselect() {
            this.getEl().removeAttribute("data-live-edit-selected");
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
    }
}