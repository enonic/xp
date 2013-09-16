module LiveEdit {

    // Uses
    var $ = $liveEdit;

    var ATTRIBUTE_NAME:string = 'data-live-edit-selected';

    export class Selection {

        public static select(component:LiveEdit.component.Component, event?:JQueryEventObject):void {

            this.setSelectionAttributeOnElement(component.getElement());

            var mouseClickPagePosition:any = null;
            if (event) {
                mouseClickPagePosition = {
                    x: event.pageX,
                    y: event.pageY
                };
            }

            $(window).trigger('selectComponent.liveEdit', [component, mouseClickPagePosition]);
        }

        public static deSelect():void {
            $(window).trigger('deselectComponent.liveEdit');
            this.clearSelection();
        }

        public static setSelectionAttributeOnElement(element:JQuery):void {
            this.clearSelection();
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

        public static clearSelection():void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}