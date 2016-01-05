module app.wizard {

    import Principal = api.security.Principal;
    import FormItemBuilder = api.ui.form.FormItemBuilder;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class AuthServiceWizardStepForm extends api.app.wizard.WizardStepForm {

        private authServiceCombobox: api.ui.security.auth.AuthServiceComboBox;
        private authServiceComboboxLoaded = false;
        private authServiceKey: string;

        constructor() {
            super();

            this.authServiceCombobox = new api.ui.security.auth.AuthServiceComboBox();
            var appComboboxLoadingListener = () => {
                this.authServiceComboboxLoaded = true;
                this.selectAuthService();
                this.authServiceCombobox.unLoaded(appComboboxLoadingListener);
            };
            this.authServiceCombobox.onLoaded(appComboboxLoadingListener);
            this.authServiceCombobox.getLoader().load();

            var authServiceFormItem = new FormItemBuilder(this.authServiceCombobox).
                setLabel('Authentication').
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(authServiceFormItem);

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
            this.authServiceKey = userStore.getAuthServiceKey();
            this.selectAuthService();
        }

        private selectAuthService(): void {
            if (this.authServiceComboboxLoaded) {
                if (this.authServiceKey) {
                    this.authServiceCombobox.getDisplayValues().
                        filter((authService: api.security.auth.AuthService) => {
                            return this.authServiceKey == authService.getKey();
                        }).
                        forEach((selectedOption: api.security.auth.AuthService) => {
                            this.authServiceCombobox.select(selectedOption);
                        });

                }
            }
        }

        getAuthServiceKey(): string {
            if (this.authServiceCombobox.countSelected() == 0) {
                return null;
            }
            return this.authServiceCombobox.getSelectedDisplayValues()[0].
                getKey();
        }

        giveFocus(): boolean {
            return this.authServiceCombobox.giveFocus();
        }
    }
}
