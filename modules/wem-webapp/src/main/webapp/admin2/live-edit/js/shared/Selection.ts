module LiveEdit {
    export class Selection {

        // fixme: everything here should work with app component object. Not DOM. See getSelectedComponent.

        // Uses
        static $ = $liveEdit;

        private static ATTRIBUTE_NAME = 'data-live-edit-selected';

        public static setSelection(element):void {
            element.attr(ATTRIBUTE_NAME, 'true');
        }

        public static getSelected():JQuery {
            return $('[' + ATTRIBUTE_NAME + ']');
        }


        public static getSelectedComponent():LiveEdit.component.Component {
            try {
                return new LiveEdit.component.Component(getSelected());

            } catch(ex) {
                return null;
            }
        }


        public static hasSelection():bool {
            return $('[' + ATTRIBUTE_NAME + ']').length > 0;
        }

        public static clearSelection():void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}