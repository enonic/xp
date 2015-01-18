module api.ui.security.acl {

    import Permission = api.security.acl.Permission;
    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import UserStoreAccessControlEntry = api.security.acl.UserStoreAccessControlEntry;

    export class UserStoreAccessControlListView extends api.ui.selector.list.ListBox<UserStoreAccessControlEntry> {

        private itemValueChangedListeners: {(item: UserStoreAccessControlEntry): void}[] = [];
        private itemsEditable: boolean = true;

        constructor(className?: string) {
            super('access-control-list' + (className ? " " + className : ""));
        }

        createItemView(entry: UserStoreAccessControlEntry): UserStoreAccessControlEntryView {
            var itemView = new UserStoreAccessControlEntryView(entry);
            itemView.setEditable(this.itemsEditable);
            itemView.onRemoveClicked(() => {
                this.removeItem(entry);
            });
            itemView.onValueChanged((item: UserStoreAccessControlEntry) => {
                this.notifyItemValueChanged(item);
            });
            return itemView;
        }

        getItemId(item: UserStoreAccessControlEntry): string {
            return item.getPrincipal().getKey().toString();
        }

        onItemValueChanged(listener: (item: UserStoreAccessControlEntry) => void) {
            this.itemValueChangedListeners.push(listener);
        }

        unItemValueChanged(listener: (item: UserStoreAccessControlEntry) => void) {
            this.itemValueChangedListeners = this.itemValueChangedListeners.filter((curr) => {
                return curr != listener;
            })
        }

        notifyItemValueChanged(item: UserStoreAccessControlEntry) {
            this.itemValueChangedListeners.forEach((listener) => {
                listener(item);
            })
        }

        setItemsEditable(editable: boolean): UserStoreAccessControlListView {
            if (this.itemsEditable != editable) {
                this.itemsEditable = editable;
                this.refreshList();
            }
            return this;
        }

        isItemsEditable(): boolean {
            return this.itemsEditable;
        }

    }

}