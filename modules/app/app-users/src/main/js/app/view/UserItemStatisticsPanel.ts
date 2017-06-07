import '../../api.ts';
import {UserTreeGridItem, UserTreeGridItemType} from '../browse/UserTreeGridItem';

import ViewItem = api.app.view.ViewItem;
import ItemStatisticsPanel = api.app.view.ItemStatisticsPanel;
import ItemDataGroup = api.app.view.ItemDataGroup;

import Principal = api.security.Principal;
import PrincipalType = api.security.PrincipalType;
import GetPrincipalByKeyRequest = api.security.GetPrincipalByKeyRequest;

import PrincipalViewer = api.ui.security.PrincipalViewer;
import i18n = api.util.i18n;

export class UserItemStatisticsPanel extends ItemStatisticsPanel<UserTreeGridItem> {

    private userDataContainer: api.dom.DivEl;

    constructor() {
        super('principal-item-statistics-panel');

        this.userDataContainer = new api.dom.DivEl('user-data-container');
        this.appendChild(this.userDataContainer);
    }

    setItem(item: ViewItem<UserTreeGridItem>) {
        let currentItem = this.getItem();

        if (!currentItem || !currentItem.equals(item)) {

            switch (item.getModel().getType()) {
            case UserTreeGridItemType.PRINCIPAL:
                this.populatePrincipalViewItem(item);
                break;
            default:

            }

            this.userDataContainer.removeChildren();

            if (item.getModel().getPrincipal()) {
                let type = item.getModel().getPrincipal().getType();

                switch (type) {
                case PrincipalType.USER:
                    this.appendUserMetadata(item);
                    break;
                case PrincipalType.GROUP:
                    this.appendGroupRoleMetadata(item);
                    break;
                case PrincipalType.ROLE:
                    this.appendGroupRoleMetadata(item);
                    break;
                }
            }

            super.setItem(item);
        }
    }

    private populatePrincipalViewItem(item: ViewItem<UserTreeGridItem>) {
        item.setPathName(item.getModel().getPrincipal().getKey().getId());
        item.setPath(item.getModel().getPrincipal().getKey().toPath(true));
        item.setIconSize(128);
    }

    private appendUserMetadata(item: ViewItem<UserTreeGridItem>) {
        // Insert an empty data first to avoid blinking, after full data is loaded.
        let userGroup = new ItemDataGroup(i18n('field.user'), 'user');
        userGroup.addDataList(i18n('field.email'), ' ');
        this.userDataContainer.appendChild(userGroup);

        let rolesAndGroupsGroup = new ItemDataGroup(i18n('field.rolesAndGroups'), 'roles-and-groups');
        rolesAndGroupsGroup.addDataArray(i18n('field.roles'), []);
        rolesAndGroupsGroup.addDataArray(i18n('field.groups'), []);
        this.userDataContainer.appendChild(rolesAndGroupsGroup);

        new GetPrincipalByKeyRequest(item.getModel().getPrincipal().getKey()).includeUserMemberships(true).sendAndParse().then(
            (principal: Principal) => {
                userGroup = new ItemDataGroup(i18n('field.user'), 'user');
                userGroup.addDataList(i18n('field.email'), principal.asUser().getEmail());

                rolesAndGroupsGroup = new ItemDataGroup(i18n('field.rolesAndGroups'), 'memeberships');

                let roles = principal.asUser().getMemberships().filter((el) => {
                    return el.isRole();
                }).map((el) => {
                    let viewer = new PrincipalViewer();
                    viewer.setObject(el);
                    return viewer;
                });
                rolesAndGroupsGroup.addDataElements(i18n('field.roles'), roles);

                let groups = principal.asUser().getMemberships().filter((el) => {
                    return el.isGroup();
                }).map((el) => {
                    let viewer = new PrincipalViewer();
                    viewer.setObject(el);
                    return viewer;
                });
                rolesAndGroupsGroup.addDataElements(i18n('field.groups'), groups);

                this.userDataContainer.removeChildren();
                this.userDataContainer.appendChild(userGroup);
                this.userDataContainer.appendChild(rolesAndGroupsGroup);
            }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }

    private appendGroupRoleMetadata(item: ViewItem<UserTreeGridItem>) {
        // Insert an empty data first to avoid blinking, after full data is loaded.
        const type = PrincipalType[item.getModel().getPrincipal().getType()];
        const name = type.charAt(0) + type.slice(1).toLowerCase();

        const groupAndRoleGroup = new ItemDataGroup(name, 'group-and-role');
        groupAndRoleGroup.appendChild(new api.dom.DivEl('description').setHtml(item.getModel().getPrincipal().getDescription()));
        this.userDataContainer.appendChild(groupAndRoleGroup);

        const membersGroup = new ItemDataGroup(i18n('field.members'), 'members');
        membersGroup.addDataArray(i18n('field.members'), []);
        this.userDataContainer.appendChild(membersGroup);

        new GetPrincipalByKeyRequest(item.getModel().getPrincipal().getKey())
            .includeUserMemberships(true)
            .sendAndParse()
            .then((principal: Principal) => {

                const membersPromises =
                    (principal.isGroup() ? principal.asGroup().getMembers() : principal.asRole().getMembers())
                        .map((el) => {
                            return new GetPrincipalByKeyRequest(el).sendAndParse();
                        });

                wemQ.all(membersPromises).then((results: Principal[]) => {

                    const newMembersGroup = new ItemDataGroup(i18n('field.members'), 'members');

                    newMembersGroup.addDataElements(i18n('field.members'), results.map((el) => {
                        const viewer = new PrincipalViewer();
                        viewer.setObject(el);
                        return viewer;
                    }));

                    this.userDataContainer.removeChildren();
                    this.userDataContainer.appendChild(groupAndRoleGroup);
                    this.userDataContainer.appendChild(newMembersGroup);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();

            }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).done();
    }
}
