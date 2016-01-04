module app.wizard {

    import Principal = api.security.Principal;
    import FormItemBuilder = api.ui.form.FormItemBuilder;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class AuthApplicationWizardStepForm extends api.app.wizard.WizardStepForm {

        private appCombobox: api.ui.security.auth.AuthServiceComboBox;
        private appComboboxLoaded = false;
        private authApplication: string;

        constructor() {
            super();

            this.appCombobox = new api.ui.security.auth.AuthServiceComboBox();
            var appComboboxLoadingListener = () => {
                this.appComboboxLoaded = true;
                this.selectAuthApplication();
                this.appCombobox.unLoaded(appComboboxLoadingListener);
            };
            this.appCombobox.onLoaded(appComboboxLoadingListener);
            this.appCombobox.getLoader().load();

            var authApplicationFormItem = new FormItemBuilder(this.appCombobox).
                setLabel('Authentication').
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(authApplicationFormItem);

            var form = new api.ui.form.Form().add(fieldSet);

            form.onFocus((event) => {
                this.notifyFocused(event);
            });
            form.onBlur((event) => {
                this.notifyBlurred(event);
            });

            form.onValidityChanged((event: api.ValidityChangedEvent) => {
                this.notifyValidityChanged(new api.app.wizard.WizardStepValidityChangedEvent(event.isValid()));
            });

            this.appendChild(form);
        }

        layout(userStore: api.security.UserStore) {
            this.authApplication = userStore.getAuthApplication();
            this.selectAuthApplication();
        }

        private selectAuthApplication(): void {
            if (this.appComboboxLoaded) {
                if (this.authApplication) {
                    this.appCombobox.getDisplayValues().
                        filter((application: api.security.auth.AuthService) => {
                            return this.authApplication == application.getKey();
                        }).
                        forEach((selectedOption: api.security.auth.AuthService) => {
                            this.appCombobox.select(selectedOption);
                        });

                }
            }
        }

        getApplication(): string {
            if (this.appCombobox.countSelected() == 0) {
                return null;
            }
            return this.appCombobox.getSelectedDisplayValues()[0].
                getKey();
        }

        giveFocus(): boolean {
            return this.appCombobox.giveFocus();
        }
    }
}
