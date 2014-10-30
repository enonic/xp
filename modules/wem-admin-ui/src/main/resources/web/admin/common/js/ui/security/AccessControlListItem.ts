module api.ui.security {

    export class AccessControlListItem extends api.dom.DivEl {

        private customConfigurationPanel: api.dom.DivEl;

        constructor() {
            super('access-control-list-item');

            var nameAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(nameAndIconView);

            nameAndIconView.setIconClass("icon-users").setMainName("Administrator").setSubName('enonic/administrator');

            var accessSelector = new AccessSelector();
            this.appendChild(accessSelector);

            var removeButton = new api.dom.AEl("icon-close");
            this.appendChild(removeButton);

            this.customConfigurationPanel = new api.dom.DivEl('permissions-configuration-panel');
            this.customConfigurationPanel.hide();
            this.appendChild(this.customConfigurationPanel);

            var permissionsList1 = new api.dom.DivEl('permissions-list-1');
            this.customConfigurationPanel.appendChild(permissionsList1);

            ['Read', 'Modify', 'Delete', 'Create', 'Publish'].forEach((label: string) => {
                permissionsList1.appendChild(new PermissionToggle(label));
            });

            var permissionsList2 = new api.dom.DivEl('permissions-list-2');
            this.customConfigurationPanel.appendChild(permissionsList2);

            ['Read Permissions', 'Write Permissions'].forEach((label: string) => {
                permissionsList2.appendChild(new PermissionToggle(label));
            });

            accessSelector.onValueChanged((event: ValueChangedEvent) => {
                if (event.getNewValue() == 'custom') {
                    this.customConfigurationPanel.show();
                } else {
                    this.customConfigurationPanel.hide();
                }
            });

        }

    }

}