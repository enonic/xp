module api.ui.security.acl {

    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;
    import Permission = api.security.acl.Permission;
    import UserStoreAccessControlEntry = api.security.acl.UserStoreAccessControlEntry;
    import UserStoreAccess = api.security.acl.UserStoreAccess;

    export class UserStoreAccessControlEntryView extends api.ui.security.PrincipalViewer {

        private ace: UserStoreAccessControlEntry;

        private accessSelector: UserStoreAccessSelector;

        private removeButton: api.dom.AEl;
        private valueChangedListeners: {(item: UserStoreAccessControlEntry): void}[] = [];
        private editable: boolean = true;

        public static debug: boolean = false;

        constructor(ace: UserStoreAccessControlEntry) {
            super();
            this.setClass('userstore-access-control-entry');
            //this.toggleClass('inherited', ace.isInherited());

            this.ace = ace;
            if (isNaN(this.ace.getAccess())) {
                this.ace.setAccess(UserStoreAccess[UserStoreAccess.CREATE_USERS]);
            }

            this.setUserStoreAccessControlEntry(this.ace, true);

        }

        getValueChangedListeners(): {(item: UserStoreAccessControlEntry): void}[] {
            return this.valueChangedListeners;
        }

        setEditable(editable: boolean) {
            if (editable != this.editable) {
                this.accessSelector.setEnabled(editable);
                this.editable = editable;
            }

            this.toggleClass('readonly', !editable);
        }

        isEditable(): boolean {
            return this.editable;
        }

        onValueChanged(listener: (item: UserStoreAccessControlEntry) => void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (item: UserStoreAccessControlEntry) => void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
                return curr != listener;
            });
        }

        notifyValueChanged(item: UserStoreAccessControlEntry) {
            this.valueChangedListeners.forEach((listener) => {
                listener(item);
            });
        }

        public setUserStoreAccessControlEntry(ace: UserStoreAccessControlEntry, silent?: boolean) {
            this.ace = ace;

            let principal = Principal.create().setKey(ace.getPrincipal().getKey()).setDisplayName(
                ace.getPrincipal().getDisplayName()).setModifiedTime(ace.getPrincipal().getModifiedTime()).build();
            this.setObject(principal);

            this.doLayout(principal);
        }

        public getUserStoreAccessControlEntry(): UserStoreAccessControlEntry {
            let ace = new UserStoreAccessControlEntry(this.ace.getPrincipal(), this.ace.getAccess());
            return ace;
        }

        doLayout(object: Principal) {
            super.doLayout(object);

            if (UserStoreAccessControlEntryView.debug) {
                console.debug('UserStoreAccessControlEntryView.doLayout');
            }

            // permissions will be set on access selector value change

            if (!this.accessSelector) {
                this.accessSelector = new UserStoreAccessSelector();
                this.accessSelector.onValueChanged((event: ValueChangedEvent) => {
                    this.ace.setAccess(event.getNewValue());
                });
                this.appendChild(this.accessSelector);
            }
            this.accessSelector.setValue(this.ace.getAccess(), true);

            if (!this.removeButton) {
                this.removeButton = new api.dom.AEl('icon-close');
                this.removeButton.onClicked((event: MouseEvent) => {
                    if (this.editable) {
                        this.notifyRemoveClicked(event);
                    }
                    event.stopPropagation();
                    event.preventDefault();
                    return false;
                });
                this.appendChild(this.removeButton);
            }
        }
    }

}
