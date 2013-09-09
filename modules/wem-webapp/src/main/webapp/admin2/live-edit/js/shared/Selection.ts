module LiveEdit {

    // Uses
    var $ = $liveEdit;

    var ATTRIBUTE_NAME:string = 'data-live-edit-selected';

    export class Selection {

        public static setSelectionOnElement(element:JQuery):void {
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

        public static pageHasSelectedElements():boolean {
            return $('[' + ATTRIBUTE_NAME + ']').length > 0;
        }

        public static clearSelection():void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}