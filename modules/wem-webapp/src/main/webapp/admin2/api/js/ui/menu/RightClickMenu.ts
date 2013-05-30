module API_ui_menu{

    export class RightClickMenu {

        //private ext:Ext.Component;

        //private buttons:Button[] = [];

        constructor(actions:API_action.Action[]) {

            for (var i in actions) {
                this.addAction(actions[i]);
            }

            //this.ext = new Ext.Component;
        }

        private addAction(action:API_action.Action) {

            //var button = new Button(action);
            //this.buttons.push(button);
        }
    }
}
