module api.ui.security {

    import Permission = api.security.acl.Permission;

    export class AccessControlListItem extends api.dom.DivEl {

        private principal: api.security.Principal;
        private accessSelector: AccessSelector;
        private permissionSelector: PermissionSelector;
        private removeClickedListeners: {(event: MouseEvent):void}[] = [];

        constructor(principal: api.security.Principal, permissions?: {allow: Permission[]; deny: Permission[]}) {
            super('access-control-list-item');

            this.principal = principal;

            var nameAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(nameAndIconView);

            var iconClass;
            switch (principal.getType()) {
            case api.security.PrincipalType.USER:
                iconClass = "icon-user";
                break;
            case api.security.PrincipalType.GROUP:
            case api.security.PrincipalType.ROLE:
                iconClass = "icon-users";
            }

            nameAndIconView.setIconClass(iconClass).
                setMainName(principal.getDisplayName()).
                setSubName(principal.getKey().toString());

            this.accessSelector = new AccessSelector();
            this.appendChild(this.accessSelector);

            var removeButton = new api.dom.AEl("icon-close");
            removeButton.onClicked((event: MouseEvent) => this.notifyRemoveClicked(event));
            this.appendChild(removeButton);

            this.permissionSelector = new PermissionSelector();
            this.appendChild(this.permissionSelector);

            this.accessSelector.onValueChanged((event: ValueChangedEvent) => {
                if (Access[event.getNewValue()] == Access.CUSTOM) {
                    this.permissionSelector.show();
                } else {
                    if (Access[event.getOldValue()] == Access.CUSTOM) {
                        this.permissionSelector.hide();
                    }
                    this.permissionSelector.setValue(this.getPermissionsValueFromAccess(Access[event.getNewValue()]));
                }
            });

            this.setPermissions(permissions);
        }

        public onValueChanged(listener: (event: api.ui.ValueChangedEvent) => void) {
            this.permissionSelector.onValueChanged(listener);
        }

        public unValueChanged(listener: (event: api.ui.ValueChangedEvent) => void) {
            this.permissionSelector.unValueChanged(listener);
        }

        public onRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners.push(listener);
        }

        public unRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners = this.removeClickedListeners.filter((current) => {
                return current !== listener;
            })
        }

        private notifyRemoveClicked(event: MouseEvent) {
            this.removeClickedListeners.forEach((listener) => {
                listener(event);
            })
        }

        public setPermissions(permissions: {allow: Permission[]; deny: Permission[]}) {
            if (!permissions) {
                permissions = {
                    allow: [],
                    deny: []
                }
            }
            this.permissionSelector.setValue(permissions);
            this.accessSelector.setValue(this.getAccessValueFromPermissions(permissions));
        }

        public getPermissions(): {allow: Permission[]; deny: Permission[]} {
            return this.permissionSelector.getValue();
        }

        public getPrincipal(): api.security.Principal {
            return this.principal;
        }

        private getPermissionsValueFromAccess(access: Access) {
            var permissions = {
                allow: [],
                deny: []
            };
            // Falls-through are intended !
            switch (access) {
            case Access.FULL:
                permissions.allow.push(Permission.READ_PERMISSIONS);
                permissions.allow.push(Permission.WRITE_PERMISSIONS);
            case Access.PUBLISH:
                permissions.allow.push(Permission.PUBLISH);
            case Access.WRITE:
                permissions.allow.push(Permission.CREATE);
                permissions.allow.push(Permission.MODIFY);
                permissions.allow.push(Permission.DELETE);
            case Access.READ:
                permissions.allow.push(Permission.READ);
                break;
            }
            return permissions;
        }

        private getAccessValueFromPermissions(permissions: {allow:Permission[]; deny:Permission[]}): Access {

            if (permissions.deny.length == 0) {
                if (this.isFullAccess(permissions.allow)) {
                    return Access.FULL;
                } else if (this.isCanPublish(permissions.allow)) {
                    return Access.PUBLISH;
                } else if (this.isCanWrite(permissions.allow)) {
                    return Access.WRITE;
                } else if (this.isCanRead(permissions.allow)) {
                    return Access.READ;
                }
            }
            return Access.CUSTOM;
        }

        private isCanRead(allowed: Permission[]): boolean {
            return allowed.indexOf(Permission.READ) >= 0;
        }

        private isCanWrite(allowed: Permission[]): boolean {
            return this.isCanRead(allowed) &&
                   allowed.indexOf(Permission.CREATE) >= 0 &&
                   allowed.indexOf(Permission.MODIFY) >= 0 &&
                   allowed.indexOf(Permission.DELETE) >= 0;
        }

        private isCanPublish(allowed: Permission[]): boolean {
            return this.isCanWrite(allowed) &&
                   allowed.indexOf(Permission.PUBLISH) >= 0;
        }

        private isFullAccess(allowed: Permission[]): boolean {
            return this.isCanPublish(allowed) &&
                   allowed.indexOf(Permission.READ_PERMISSIONS) >= 0 &&
                   allowed.indexOf(Permission.WRITE_PERMISSIONS) >= 0;
        }


    }

}