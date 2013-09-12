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

            var componentTypeName = componentHelper.getComponentTypeFromElement(element).toUpperCase();
            var componentTypeEnum = LiveEdit.component.Type[componentTypeName];

            this.setComponentType(new LiveEdit.component.ComponentType( componentTypeEnum ));
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
            // We need to dynamically get the dimension as it can change on eg. browser window resize.
            return componentHelper.getDimensionsFromElement(this.getElement());
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

    }
}
