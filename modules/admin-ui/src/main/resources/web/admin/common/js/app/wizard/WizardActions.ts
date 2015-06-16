module api.app.wizard {

    export class WizardActions<T> {

        private actions: api.ui.Action[];

        private suspendedActions: api.ui.Action[] = [];

        constructor(...actions: api.ui.Action[]) {
            this.actions = actions;
        }

        enableActionsForNew() {
            throw new Error('Must be overridden by inheritors');
        }

        enableActionsForExisting(existing: T) {
            throw new Error('Must be overridden by inheritors');
        }

        getActions(): api.ui.Action[] {
            return this.actions;
        }

        suspendActions(suspend: boolean = true) {
            if (suspend) {
                this.actions.forEach((action) => {
                    if (action.isEnabled()) {
                        this.suspendedActions.push(action);
                        action.setEnabled(false);
                    }
                });
            } else {
                this.suspendedActions.forEach(action => action.setEnabled(true));
                this.suspendedActions.length = 0;
            }
        }
    }
}