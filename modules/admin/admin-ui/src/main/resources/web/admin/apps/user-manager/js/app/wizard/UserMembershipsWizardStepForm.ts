module app.wizard {

    import User = api.security.User;
    import Principal = api.security.Principal;
    import PrincipalKey = api.security.PrincipalKey;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalLoader = api.security.PrincipalLoader;
    import RoleKeys = api.security.RoleKeys;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import Validators = api.ui.form.Validators;

    import PrincipalComboBox = api.ui.security.PrincipalComboBox;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;

    export class UserMembershipsWizardStepForm extends api.app.wizard.WizardStepForm {

        private groups: PrincipalComboBox;

        private roles: PrincipalComboBox;

        private principal: Principal;

        private groupsLoaded: boolean;

        private rolesLoaded: boolean;

        constructor() {
            super("user-memberships");

            this.groupsLoaded = false;
            this.rolesLoaded = false;

            var groupsLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.GROUP]);
            var rolesLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.ROLE]).
                skipPrincipals([RoleKeys.EVERYONE]);

            this.groups = new PrincipalComboBox(groupsLoader);
            groupsLoader.load();
            this.roles = new PrincipalComboBox(rolesLoader);
            rolesLoader.load();

            var groupsHandler = () => { this.groupsLoaded = true; this.selectMembership(); this.groups.unLoaded(groupsHandler); };
            var rolesHandler = () => { this.rolesLoaded = true; this.selectMembership(); this.roles.unLoaded(rolesHandler); };

            this.groups.onLoaded(groupsHandler);
            this.roles.onLoaded(rolesHandler);

            var groupsFormItem = new FormItemBuilder(this.groups).
                setLabel('Groups').
                build();

            var rolesFormItem = new FormItemBuilder(this.roles).
                setLabel('Roles').
                build();

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(groupsFormItem);
            fieldSet.add(rolesFormItem);

            var form = new api.ui.form.Form().add(fieldSet);

            this.appendChild(form);

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
            this.selectMembership();
        }

        private selectMembership(): void {
            if (!!this.principal && this.groupsLoaded && this.rolesLoaded) {

                var groups = this.principal.asUser().getMemberships().
                    filter((el) => { return el.isGroup()}).
                    map((el) => { return el.getKey().getId(); });

                var roles = this.principal.asUser().getMemberships().
                    filter((el) => { return el.isRole()}).
                    map((el) => { return el.getKey().getId(); });

                this.groups.getDisplayValues().filter((principal: Principal) => {
                    return groups.indexOf(principal.getKey().getId()) >= 0;
                }).forEach((selection) => {
                    this.groups.select(selection);
                });

                this.roles.getDisplayValues().filter((principal: Principal) => {
                    return roles.indexOf(principal.getKey().getId()) >= 0;
                }).forEach((selection) => {
                    this.roles.select(selection);
                });
            }
        }

        getMemberships(): Principal[] {
            return this.groups.getSelectedDisplayValues().
                concat(this.roles.getSelectedDisplayValues()).
                map((el) => { return Principal.fromPrincipal(el); });
        }

        giveFocus(): boolean {
            return this.groups.giveFocus();
        }
    }
}
