interface ElementDimensions {
    top: number;
    left: number;
    width: number;
    height: number;
}

module LiveEdit.component {

    export class Component {

        element:JQuery;

        componentType:ComponentType;

        key:string;

        name:string;

        elementDimensions:ElementDimensions;

        selectedAsParent:boolean = false;

        constructor(element:JQuery) {

            if (element.length == 0) {
                throw "Could not create component. No element";
            }

            this.setElement(element);

            this.setName(this.getComponentNameFromElement());

            this.setKey(this.getComponentKeyFromElement());

            this.setElementDimensions(this.getDimensionsFromElement());

            this.setComponentType(new LiveEdit.component.ComponentType( this.resolveComponentTypeEnum() ));
        }

        getElement():JQuery {
            return this.element;
        }

        setElement(element:JQuery):void {
            this.element = element;
        }

        getName():string {
            return this.name;
        }

        setName(name:string):void {
            this.name = name;
        }

        getKey():string {
            return this.key;
        }

        setKey(key:string):void {
            this.key = key || '';
        }

        getElementDimensions():ElementDimensions {
            // We need to dynamically get the dimension as it can change on eg. browser window resize.
            return this.getDimensionsFromElement();
        }

        setElementDimensions(dimensions:ElementDimensions):void {
            this.elementDimensions = dimensions;
        }

        setComponentType(componentType:ComponentType):void {
            this.componentType = componentType;
        }

        getComponentType():ComponentType {
            return this.componentType;
        }

        isEmpty():boolean {
            return this.getElement().attr('data-live-edit-empty-component') == 'true';
        }

        isSelected():boolean {
            return this.getElement().attr('data-live-edit-selected') == 'true';
        }

        setSelectedAsParent(value:boolean) {
            this.selectedAsParent = value;
        }

        isSelectedAsParent():boolean {
            return this.selectedAsParent;
        }

        private resolveComponentTypeEnum():LiveEdit.component.Type {
            var elementComponentTypeName = this.getComponentTypeNameFromElement().toUpperCase();
            return LiveEdit.component.Type[elementComponentTypeName];
        }

        private getComponentTypeNameFromElement():string {
            return this.element.data('live-edit-type');
        }

        private getComponentKeyFromElement():string {
            return this.element.data('live-edit-key');
        }

        private getComponentNameFromElement():string {
            return this.element.data('live-edit-name') || '[No Name]';
        }

        private getDimensionsFromElement():ElementDimensions {
            var cmp:JQuery = this.element;
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
