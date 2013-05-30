module API_ui_toolbar {

    export class Button {

        private action:API_action.Action;

        constructor(action:API_action.Action) {
            this.action = action;
            action.addPropertyChangeListener((action:API_action.Action) => {
                this.action = action;
            });
        }

        toHTML():string {
            var html = ""
            html += "<button>";
            html += this.action.getLabel();
            html += "</button>";
            return html;
        }

    }
}
