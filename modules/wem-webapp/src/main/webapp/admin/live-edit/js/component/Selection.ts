module LiveEdit.component {

    // Uses
    var $ = $liveEdit;

    export var ATTRIBUTE_NAME:string = 'data-live-edit-selected';

    export class Selection {

        public static select(component:LiveEdit.component.Component, event?:JQueryEventObject):void {
            console.log(component);

            this.setSelectionAttributeOnElement(component.getElement());

            var mouseClickPagePosition:any = null;
            if (event) {
                mouseClickPagePosition = {
                    x: event.pageX,
                    y: event.pageY
                };
            }
            if (component.componentType.getType() == Type.PAGE) {
                $(window).trigger('selectPage.liveEdit', [component, mouseClickPagePosition]);
            } else if (component.getComponentType().getType() == Type.REGION) {
                $(window).trigger('selectRegion.liveEdit', [component, component.getName(), mouseClickPagePosition]);
            } else {
                $(window).trigger('selectComponent.liveEdit', [component, component.getName(), mouseClickPagePosition]);
            }

        }

        public static deselect():void {
            $(window).trigger('deselectComponent.liveEdit');
            this.removeSelectedAttribute();
        }

        public static setSelectionAttributeOnElement(element:JQuery):void {
            this.removeSelectedAttribute();
            element.attr(ATTRIBUTE_NAME, 'true');
        }

        public static getSelectedComponent():LiveEdit.component.Component {
            try {
                return new LiveEdit.component.Component($('[' + ATTRIBUTE_NAME + ']'));

            } catch(ex) {
                return null;
            }
        }

        public static pageHasSelectedElement():boolean {
            return $('[' + ATTRIBUTE_NAME + ']').length > 0;
        }

        public static removeSelectedAttribute():void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}