module LiveEdit.component.helper {

    export class DragHelper {

        public static createDragHelperHtml():string {
            var html:string;
            // Note: The width and height must be inlined. If not jQueryUI will overwrite.
            // TODO: Why adding both classes initially? Should it not
            html =
            '<div id="live-edit-drag-helper" class="live-edit-font-icon-drop-allowed live-edit-font-icon-drop-not-allowed" style="width: 48px; height: 48px; position: absolute; z-index: 400000;" data-live-edit-drop-allowed="false"></div>';
            return html;
        }

        public static updateStatusIcon(dropAllowed) {
            var helper:JQuery = wemjq('#live-edit-drag-helper');
            if (dropAllowed) {
                helper.removeClass("live-edit-font-icon-drop-not-allowed");
                helper.addClass("live-edit-font-icon-drop-allowed");
            } else {
                helper.removeClass("live-edit-font-icon-drop-allowed");
                helper.addClass("live-edit-font-icon-drop-not-allowed");
            }

        }

    }
}
