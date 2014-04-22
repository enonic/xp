module LiveEdit.component {

    export interface ElementDimensions {
        top: number;
        left: number;
        width: number;
        height: number;
    }

    export class Component extends api.dom.Element {

        element: JQuery;
        componentType: ComponentType;
        elementDimensions: ElementDimensions;
        selectedAsParent: boolean;

        private mask: api.ui.LoadMask;

        constructor(element?: HTMLElement) {
            this.selectedAsParent = false;
            var props = new api.dom.ElementProperties();
            props.setGenerateId(false);
            if (element) {
                props.setHelper(new api.dom.ElementHelper(element));
            } else {
                props.setTagName("div");
            }
            super(props);
            this.setElementDimensions(this.getDimensionsFromElement());
            if (!this.componentType) {
                this.setComponentType(new LiveEdit.component.ComponentType(this.resolveComponentTypeEnum()));
            }

            this.mask = new api.ui.LoadMask(this);
            this.appendChild(this.mask);
        }

        public static fromJQuery(element: JQuery): Component {
            return  new Component(element.get(0));
        }

        public static fromElement(element: HTMLElement): Component {
            return new Component(element);
        }

        getRegionName():string {
            var regionEl = api.dom.Element.fromHtmlElement($liveEdit(this.getElement()).parents('[data-live-edit-region]'));
            return regionEl.getEl().getData('live-edit-region');
        }

        setComponentPath(path:string) {
            this.getEl().setData('live-edit-component', path);
        }

        getComponentName(): string {
            if (this.getComponentType().getType() == Type.PAGE) {
                return content ? content.getDisplayName() : '[No Name]';
            } else if (this.getComponentType().getType() == Type.REGION) {
                var regionPath = this.getEl().getData('live-edit-region');
                return regionPath ? regionPath.substring(regionPath.lastIndexOf('/') + 1) : '[No Name]';
            } else {
                var path = this.getComponentPath();
                return path ? path.substring(path.lastIndexOf('/') + 1) : '[No Name]';
            }
        }

        getComponentPath(): string {
            return this.getEl().getData('live-edit-component');
        }

        getPrecedingComponentPath(): string {
            var previousComponent = api.dom.Element.fromHtmlElement($liveEdit(this.getHTMLElement()).prevAll('[data-live-edit-component]')[0]);
            return previousComponent.getEl().getData("live-edit-component");
        }

        getItemId(): number {
            return parseInt(this.getEl().getData("itemid"));
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

        hasComponentPath(): boolean {
            if (this.getElement().data('live-edit-component')) {
                return true;
            } else {
                return false;
            }
        }

        isEmpty(): boolean {
            return this.getElement().attr('data-live-edit-empty-component') == 'true';
        }

        isSelected(): boolean {
            return this.getElement().attr('data-live-edit-selected') == 'true';
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

            var type =  this.getEl().getData('liveEditType');
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
