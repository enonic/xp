module LiveEdit.component {

    // Uses
    var componentHelper = LiveEdit.component.ComponentHelper;

    export class Component {

        private componentType:ComponentType;

        private element:JQuery;

        private name:string;

        private key:string;

        private elementDimensions:ElementDimensions;

        constructor(element:JQuery) {

            if (element.length == 0) {
                throw "Could not create component. No element";
            }

            this.setElement(element);
            this.setName(componentHelper.getComponentName(element));
            this.setKey(componentHelper.getComponentKeyFromElement(element));
            this.setElementDimensions(componentHelper.getDimensionsFromElement(element));
            this.setComponentType(new LiveEdit.component.ComponentType( componentHelper.getComponentTypeFromElement(element) ));
        }

        getElement():JQuery {
            return this.element;
        }

        setElement(jQueryObject:JQuery):void {
            this.element = jQueryObject;
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
            return this.elementDimensions;
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

        isEmpty():bool {
            return this.getElement().attr('data-live-edit-empty-component') == 'true';
        }

        isSelected():bool {
            return this.getElement().attr('data-live-edit-selected') == 'true';
        }

    }
}
