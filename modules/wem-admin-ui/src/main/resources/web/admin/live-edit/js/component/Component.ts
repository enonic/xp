module LiveEdit.component {

    import ItemType = api.liveedit.ItemType;
    import PageItemType = api.liveedit.PageItemType;
    import RegionItemType = api.liveedit.RegionItemType;

    export interface ElementDimensions {
        top: number;
        left: number;
        width: number;
        height: number;
    }

    export class Component extends api.liveedit.ItemView {

        element: JQuery;
        elementDimensions: ElementDimensions;
        selectedAsParent: boolean;

        constructor(type: ItemType, element?: HTMLElement, dummy?: boolean) {
            this.selectedAsParent = false;
            super(type, element, dummy);
            this.setElementDimensions(this.getDimensionsFromElement());
        }

        public static fromJQuery(element: JQuery, dummy: boolean = true): Component {
            return new Component(ItemType.fromJQuery(element), element.get(0), dummy);
        }

        public static fromElement(element: HTMLElement, dummy: boolean = true): Component {
            var itemType = ItemType.fromHTMLElement(element);
            return new Component(itemType, element, dummy);
        }

        getComponentName(): string {
            if (this.getType().equals(PageItemType.get())) {
                return content ? content.getDisplayName() : '[No Name]';
            }
            else if (this.getType().equals(RegionItemType.get())) {
                var regionPath = this.getEl().getData('live-edit-region');
                return regionPath ? regionPath.substring(regionPath.lastIndexOf('/') + 1) : '[No Name]';
            }
            else {
                var path = this.getComponentPath();
                return path ? path.getComponentName().toString() : '[No Name]';
            }
        }

        getElement(): JQuery {
            return $(this.getHTMLElement());
        }

        getElementDimensions(): ElementDimensions {
            // We need to dynamically get the dimension as it can change on eg. browser window resize.
            return this.getDimensionsFromElement();
        }

        setElementDimensions(dimensions: ElementDimensions): void {
            this.elementDimensions = dimensions;
        }

        setSelectedAsParent(value: boolean) {
            this.selectedAsParent = value;
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

    }
}
