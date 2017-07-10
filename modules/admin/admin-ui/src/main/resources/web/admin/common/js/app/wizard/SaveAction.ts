module api.app.wizard {

    import i18n = api.util.i18n;

    export class SaveAction extends api.ui.Action {

        constructor(wizardPanel: WizardPanel<any>, label: string = i18n('action.save')) {
            super(label, 'mod+s', true);

            this.onExecuted(() => {

                this.setEnabled(false);

                return wizardPanel.saveChanges().
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    finally(() => this.setEnabled(true));
            });
        }
    }
}
