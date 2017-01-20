import '../../api.ts';
import {UserTreeGridItem, UserTreeGridItemType} from './UserTreeGridItem';

export class UserTreeGridItemViewer extends api.ui.NamesAndIconViewer<UserTreeGridItem> {

    constructor() {
        super();
    }

    resolveDisplayName(object: UserTreeGridItem): string {
        return object.getItemDisplayName();
    }

    resolveUnnamedDisplayName(object: UserTreeGridItem): string {
        return object.getPrincipal() ? object.getPrincipal().getTypeName()
            : object.getUserStore() ? 'User Store' : '';
    }

    resolveSubName(object: UserTreeGridItem, relativePath: boolean = false): string {

        if (object.getType() != null) {
            switch (object.getType()) {
                case UserTreeGridItemType.USER_STORE:
                    return ('/' + object.getUserStore().getKey().toString());
                case UserTreeGridItemType.PRINCIPAL:
                    return relativePath ? object.getPrincipal().getKey().getId() :
                           object.getPrincipal().getKey().toPath();
                default:
                    return object.getItemDisplayName().toLocaleLowerCase();
            }
        }
        return '';
    }

    resolveIconClass(object: UserTreeGridItem): string {

        switch (object.getType()) {
            case UserTreeGridItemType.USER_STORE:
                return 'icon-address-book icon-large';
            case UserTreeGridItemType.PRINCIPAL:
                if (object.getPrincipal().isRole()) {
                    return 'icon-masks icon-large';
                } else if (object.getPrincipal().isGroup()) {
                    return 'icon-users icon-large';
                } else { // object.getPrincipal().isUser()
                    return 'icon-user icon-large';
                }
            case UserTreeGridItemType.GROUPS:
                return 'icon-folder icon-large';
            case UserTreeGridItemType.ROLES:
                return 'icon-folder icon-large';
            default: // UserTreeGridItemType.USERS:
                return 'icon-folder icon-large';
        }
    }
}
