module LiveEdit {
    export class Selection {

        // Uses
        static $ = $liveEdit;

        private static ATTRIBUTE_NAME = 'data-live-edit-selected';

        public static setSelection(element):void {
            element.attr(ATTRIBUTE_NAME, 'true');
        }

        public static getSelected():JQuery {
            return $('[' + ATTRIBUTE_NAME + ']');
        }

        public static hasSelection():bool {
            return $('[' + ATTRIBUTE_NAME + ']').length > 0;
        }

        public static clearSelection():void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}