module api.liveedit {

    import ComponentPath = api.content.page.ComponentPath;

    export interface ElementDimensions {
        top: number;
        left: number;
        width: number;
        height: number;
    }

    export class ItemView extends api.dom.Element {

        private type: ItemType;

        private loadMask: api.ui.LoadMask;

        private elementDimensions: ElementDimensions;

        constructor(type: ItemType, element?: HTMLElement, dummy?: boolean) {
            api.util.assertNotNull(type, "type cannot be null");
            this.type = type;

            var props = new api.dom.ElementProperties();
            props.setGenerateId(false);
            if (element) {
                props.setHelper(new api.dom.ElementHelper(element));
            }
            else {
                props.setTagName("div");
            }
            super(props);

            if (!dummy) {
                this.loadMask = new api.ui.LoadMask(this);
                this.appendChild(this.loadMask);
            }

            this.setElementDimensions(this.getDimensionsFromElement());

        }

        getElement(): JQuery {
            return $(this.getHTMLElement());
        }

        getType(): ItemType {
            return this.type;
        }

        isEmpty(): boolean {
            return this.getEl().hasAttribute('data-live-edit-empty-component');
        }

        isSelected(): boolean {
            return this.getEl().hasAttribute('data-live-edit-selected');
        }

        select() {
            this.getEl().setData("live-edit-selected", "true");
        }

        deselect() {
            this.getEl().removeAttribute("data-live-edit-selected");
            new PageComponentDeselectEvent(this).fire();
        }

        setComponentPath(path: ComponentPath) {
            this.getEl().setData('live-edit-component', path.toString());
        }

        hasComponentPath(): boolean {
            return this.getEl().hasAttribute('data-live-edit-component');
        }

        getComponentPath(): ComponentPath {
            var asString = this.getEl().getData('live-edit-component');
            return api.content.page.ComponentPath.fromString(asString);
        }

        getComponentName(): string {

            var path = this.getComponentPath();
            return path ? path.getComponentName().toString() : '[No Name]';
        }

        getParentRegion(): RegionView {
            api.util.assert(this.type.isPageComponentType(),
                    "Expected to only be called when this is a PageComponent: " + api.util.getClassName(this));

            var parentElement = this.getHTMLElement().parentElement;
            if (!parentElement) {
                return null;
            }
            var type = ItemType.fromHTMLElement(parentElement);
            if (!type || !type.equals(RegionItemType.get())) {
                return null;
            }

            return RegionView.fromHTMLElement(parentElement);
        }

        getPrecedingComponentPath(): ComponentPath {
            api.util.assert(this.type.isPageComponentType(),
                    "Expected to only be called when this is a PageComponent: " + api.util.getClassName(this));

            var previousElement = this.getPreviousElement();
            if (!previousElement) {
                return null;
            }

            var asString = previousElement.getEl().getData('live-edit-component');
            return api.content.page.ComponentPath.fromString(asString);
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

        public static fromHTMLElement(element: HTMLElement, dummy: boolean = true): ItemView {

            var type = ItemType.fromHTMLElement(element);
            return type.createView(element, dummy);
        }

        public static fromJQuery(element: JQuery, dummy: boolean = true): ItemView {
            return ItemView.fromHTMLElement(element.get(0), dummy)
        }
    }
}