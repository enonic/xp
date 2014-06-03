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

        setItemId(value: number) {
            this.getEl().setAttribute("data-live-edit-id", "" + value);
        }

        getItemId() : number {
            return +this.getEl().getAttribute("data-live-edit-id");
        }

        static parseItemId(element: HTMLElement) : number {
            var attribute = element.getAttribute("data-live-edit-id");
            if( api.util.isStringEmpty(attribute) )
            {
                return -1;
            }
            return +element.getAttribute("data-live-edit-id");
        }

        getElement(): JQuery {
            return wemjq(this.getHTMLElement());
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

        static findParentItemViewAsHTMLElement(htmlElement: HTMLElement) : HTMLElement {

            var parentHTMLElement = htmlElement.parentElement;
            var parseItemId = ItemView.parseItemId(parentHTMLElement);
            while( parseItemId == -1 ) {
                parentHTMLElement = parentHTMLElement.parentElement;
                parseItemId = ItemView.parseItemId(parentHTMLElement);
            }

            return parentHTMLElement;
        }
    }
}