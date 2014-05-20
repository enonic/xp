module LiveEdit.component {

    import ItemType = api.liveedit.ItemType;

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

        private mask: api.ui.LoadMask;

        constructor(type: ItemType, element?: HTMLElement, dummy?: boolean) {
            this.selectedAsParent = false;
            super(type, element);
            this.setElementDimensions(this.getDimensionsFromElement());
            if (!this.componentType) {
                this.setComponentType(new LiveEdit.component.ComponentType(this.resolveComponentTypeEnum()));
            }

            if (!dummy) {
                this.mask = new api.ui.LoadMask(this);
                this.appendChild(this.mask);
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
            if (this.getComponentType().getType() == Type.PAGE) {
                return content ? content.getDisplayName() : '[No Name]';
            } else if (this.getComponentType().getType() == Type.REGION) {
                var regionPath = this.getEl().getData('live-edit-region');
                return regionPath ? regionPath.substring(regionPath.lastIndexOf('/') + 1) : '[No Name]';
            } else {
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

        isSelectedAsParent(): boolean {
            return this.selectedAsParent;
        }

        private resolveComponentTypeEnum(): LiveEdit.component.Type {
            var elementComponentTypeName = this.getComponentTypeNameFromElement().toUpperCase();
            return LiveEdit.component.Type[elementComponentTypeName];
        }

        private getComponentTypeNameFromElement(): string {

            var type = this.getEl().getData('liveEditType');
            return type;
        }

        showLoadingSpinner() {
            this.mask.show();
        }

        hideLoadingSpinner() {
            this.mask.hide();
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
