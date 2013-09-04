module LiveEdit {
    export class Selection {

        // Uses
        static $ = $liveEdit;

        private static ATTRIBUTE_NAME = 'data-live-edit-selected';

        public static setSelectionOnElement(element:JQuery):void {
            clearSelection();
            element.attr(ATTRIBUTE_NAME, 'true');
        }

        public static getSelectedComponent():LiveEdit.component.Component {
            try {
                return new LiveEdit.component.Component($('[' + ATTRIBUTE_NAME + ']'));

            } catch(ex) {
                return null;
            }
        }

        public static pageHasSelectedElements():bool {
            return $('[' + ATTRIBUTE_NAME + ']').length > 0;
        }

        public static clearSelection():void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}