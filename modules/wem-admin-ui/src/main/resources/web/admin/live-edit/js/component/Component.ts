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
        componentType: ComponentType;
        elementDimensions: ElementDimensions;
        selectedAsParent: boolean;

        constructor(type: ItemType, element?: HTMLElement, dummy?: boolean) {
            this.selectedAsParent = false;
            super(type, element, dummy);
            this.setElementDimensions(this.getDimensionsFromElement());
            if (!this.componentType) {
                this.setComponentType(new LiveEdit.component.ComponentType(this.resolveComponentTypeEnum()));
            }
        }

        public static fromJQuery(element: JQuery, dummy: boolean = true): Component {
            return new Component(ItemType.fromJQuery(element), element.get(0), dummy);
        }

        public static fromElement(element: HTMLElement, dummy: boolean = true): Component {
            var itemType = ItemType.fromHTMLElement(element);
            return new Component(itemType, element, dummy);
        }

        getComponentName(): string {
            if (this.getType() == PageItemType.get()) {
                return content ? content.getDisplayName() : '[No Name]';
            }
            else if (this.getType() == RegionItemType.get()) {
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

        setComponentType(componentType: ComponentType): void {
            this.componentType = componentType;
        }

        getComponentType(): ComponentType {
            return this.componentType;
        }

        setSelectedAsParent(value: boolean) {
            this.selectedAsParent = value;
        }

        private resolveComponentTypeEnum(): LiveEdit.component.Type {
            var elementComponentTypeName = this.getComponentTypeNameFromElement().toUpperCase();
            return LiveEdit.component.Type[elementComponentTypeName];
        }

        private getComponentTypeNameFromElement(): string {

            var type = this.getEl().getData('liveEditType');
            return type;
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
