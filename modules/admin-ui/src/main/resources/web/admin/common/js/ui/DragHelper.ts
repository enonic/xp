module api.ui {

    export class DragHelper {

        private static html = '<div id="drag-helper" class="drop-allowed" style="width: 48px; height: 48px; position: absolute;"></div>';

        public static getHtml():string {
            return DragHelper.html;
        }

        public static setDropAllowed(isAllowed: boolean) {
            var helper: HTMLElement = document.getElementById('drag-helper');

            if (!helper) {
                console.warn('There is no drag helper to change its state.');
                return;
            }

            var helperEl = new api.dom.ElementHelper(helper);
            if (isAllowed) {
                helperEl.removeClass("drop-not-allowed").addClass("drop-allowed");
            } else {
                helperEl.removeClass("drop-allowed").addClass("drop-not-allowed");
            }
        }

    }
}
