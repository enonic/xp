module app.wizard {

    import Role = api.security.Role;
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalLoader = api.security.PrincipalLoader;
    import FormItemBuilder = api.ui.form.FormItemBuilder;

    import PrincipalComboBox = api.ui.security.PrincipalComboBox;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;

    export class PrincipalMembersWizardStepForm extends api.app.wizard.WizardStepForm {

        private principals: PrincipalComboBox;

        private label: LabelEl;

        private principal: Principal;

        private loader: PrincipalLoader;

        constructor(loadedHandler?: Function) {
            super();

            loadedHandler = loadedHandler || (() => {});
            this.loader = new PrincipalLoader().setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER]).
                skipPrincipals([PrincipalKey.ofAnonymous()]);

            this.principals = new PrincipalComboBox(this.loader);
            var handler = () => { this.selectMembers(); loadedHandler(); this.principals.unLoaded(handler); };
            this.principals.onLoaded(handler);

            var principalsFormItem = new FormItemBuilder(this.principals).
                setLabel('Has Role').
                build();

            this.label = principalsFormItem.getLabel();// new LabelEl("", this.principals, "input-label");

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(principalsFormItem);

            var form = new api.ui.form.Form().add(fieldSet);

            form.onFocus((event) => {
                this.notifyFocused(event);
            });
            form.onBlur((event) => {
                this.notifyBlurred(event);
            });

            this.appendChild(form);

        }

        layout(principal: Principal) {
            this.principal = principal;
            this.loader.skipPrincipal(principal.getKey());
            this.selectMembers();
        }

        private selectMembers(): void {
            if (!!this.principal) {
                var principalKeys = this.getPrincipalMembers().map((key: PrincipalKey) => {
                    return key.getId();
                });
                var selected = this.principals.getDisplayValues().filter((principal: Principal) => {
                    return principalKeys.indexOf(principal.getKey().getId()) >= 0;
                });
                selected.forEach((selection) => {
                    this.principals.select(selection);
                });
            }
        }

        getMembers(): Principal[] {
            return this.principals.getSelectedDisplayValues();
        }

        getPrincipals(): PrincipalComboBox {
            return this.principals;
        }

        getLabel(): LabelEl {
            return this.label;
        }

        getPrincipal(): Principal {
            return this.principal;
        }

        getPrincipalMembers(): PrincipalKey[] {
            throw new Error("Must be implemented by inheritors");
        }

        giveFocus(): boolean {
            return this.principals.giveFocus();
        }

        getLoader(): PrincipalLoader {
            return this.loader;
        }
    }
}
