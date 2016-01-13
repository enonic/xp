module app.wizard {

    import Principal = api.security.Principal;
    import FormItemBuilder = api.ui.form.FormItemBuilder;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;


    export class AuthApplicationWizardStepForm extends api.app.wizard.WizardStepForm {

        private authApplicationCombobox: api.ui.security.auth.AuthApplicationComboBox;
        private authApplicationComboboxLoaded = false;
        private authApplicationKey: string;

        constructor() {
            super();

            this.authApplicationCombobox = new api.ui.security.auth.AuthApplicationComboBox();
            var appComboboxLoadingListener = () => {
                this.authApplicationComboboxLoaded = true;
                this.selectAuthApplication();
                this.authApplicationCombobox.unLoaded(appComboboxLoadingListener);
            };
            this.authApplicationCombobox.onLoaded(appComboboxLoadingListener);
            this.authApplicationCombobox.getLoader().load();

            var authApplicationFormItem = new FormItemBuilder(this.authApplicationCombobox).
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
            this.authApplicationKey = userStore.getAuthConfig() ? userStore.getAuthConfig().getApplicationKey().toString() : null;
            this.selectAuthApplication();
        }

        private selectAuthApplication(): void {
            if (this.authApplicationComboboxLoaded) {
                if (this.authApplicationKey) {
                    this.authApplicationCombobox.getDisplayValues().
                        filter((authApplication: api.application.Application) => {
                            return this.authApplicationKey == authApplication.getApplicationKey().toString();
                        }).
                        forEach((selectedOption: api.application.Application) => {
                            this.authApplicationCombobox.select(selectedOption);
                        });

                }
            }
        }

        getAuthConfig(): api.security.UserStoreAuthConfig {
            if (this.authApplicationCombobox.countSelected() == 0) {
                return null;
            }

            var selectedOption = this.authApplicationCombobox.getSelectedOptions()[0];
            var selectedOptionView: api.ui.security.auth.AuthApplicationSelectedOptionView = selectedOption.getOptionView();
            return selectedOptionView.getAuthConfig();
        }

        giveFocus(): boolean {
            return this.authApplicationCombobox.giveFocus();
        }
    }
}