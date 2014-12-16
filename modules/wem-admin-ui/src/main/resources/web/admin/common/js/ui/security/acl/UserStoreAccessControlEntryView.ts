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

        private valueChangedListeners: {(item: UserStoreAccessControlEntry): void}[] = [];
        private editable: boolean = true;

        constructor(ace: UserStoreAccessControlEntry) {
            super();
            this.setClass('userstore-access-control-entry');
            //this.toggleClass('inherited', ace.isInherited());

            this.ace = ace;
            if (!this.ace.getAccess()) {
                this.ace.setAccess(UserStoreAccess[UserStoreAccess.CREATE_USERS]);
            }


            this.accessSelector = new UserStoreAccessSelector();
            this.appendChild(this.accessSelector);

            this.accessSelector.onValueChanged((event: ValueChangedEvent) => {
                this.ace.setAccess(event.getNewValue());
            })

            var removeButton = new api.dom.AEl("icon-close");
            removeButton.onClicked((event: MouseEvent) => this.notifyRemoveClicked(event));
            this.appendChild(removeButton);

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
            })
        }

        notifyValueChanged(item: UserStoreAccessControlEntry) {
            this.valueChangedListeners.forEach((listener) => {
                listener(item);
            })
        }

        public setUserStoreAccessControlEntry(ace: UserStoreAccessControlEntry, silent?: boolean) {
            this.ace = ace;

            var principal = new Principal(
                ace.getPrincipal().getKey(), ace.getPrincipal().getDisplayName(), ace.getPrincipal().getModifiedTime());
            this.setObject(principal);

            this.accessSelector.setValue(ace.getAccess(), silent);


            // permissions will be set on access selector value change
        }

        public getUserStoreAccessControlEntry(): UserStoreAccessControlEntry {
            var ace = new UserStoreAccessControlEntry(this.ace.getPrincipal(), this.ace.getAccess());
            return ace;
        }

    }

}