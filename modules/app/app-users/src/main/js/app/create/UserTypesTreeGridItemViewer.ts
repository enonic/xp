import '../../api.ts';
import {UserTypeTreeGridItem} from './UserTypeTreeGridItem';
import UserStore = api.security.UserStore;
import User = api.security.User;
import Group = api.security.Group;
import Role = api.security.Role;
import i18n = api.util.i18n;

export class UserTypesTreeGridItemViewer extends api.ui.NamesAndIconViewer<UserTypeTreeGridItem> {

    private rootViewer: boolean;

    constructor(rootViewer: boolean = true) {
        super(rootViewer ? 'root-viewer' : '');

        this.rootViewer = rootViewer;
    }

    resolveDisplayName(object: UserTypeTreeGridItem): string {
        return object.getUserItem().getDisplayName();
    }

    resolveSubName(object: UserTypeTreeGridItem, relativePath: boolean = false): string {
        return this.rootViewer ? '' : ('/' + object.getUserItem().getKey().toString());
    }

    resolveIconClass(object: UserTypeTreeGridItem): string {
        const userItem = object.getUserItem();
        if (userItem instanceof UserStore) {
            return 'icon-address-book icon-large';
        } else if (userItem instanceof User) {
            return 'icon-user icon-large';
        } else if (userItem instanceof Group) {
            return 'icon-users icon-large';
        } else if (userItem instanceof Role) {
            return 'icon-masks icon-large';
        }
        return '';
    }
}
