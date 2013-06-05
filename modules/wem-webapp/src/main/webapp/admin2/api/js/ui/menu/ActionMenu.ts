module api_ui_menu{

    export class ActionMenu {

        //private ext:Ext.Component;

        //private buttons:Button[] = [];

        constructor(actions:api_action.Action[]) {

            for (var i in actions) {
                this.addAction(actions[i]);
            }

            //this.ext = new Ext.Component;
        }

        private addAction(action:api_action.Action) {

            //var button = new Button(action);
            //this.buttons.push(button);
        }
    }
}
