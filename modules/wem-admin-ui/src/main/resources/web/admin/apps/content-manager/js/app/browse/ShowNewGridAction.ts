module app.browse {
    
    import Action = api.ui.Action;

    export class ShowNewGridAction extends Action {

        constructor() {
            super("NG", "mod+i");
            this.setEnabled(true);
            this.onExecuted(() => {
                new ShowNewContentGridEvent().fire();
            });
        }
    }
}
