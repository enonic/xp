import '../../api.ts';
import {UserTreeGridItem, UserTreeGridItemType} from '../browse/UserTreeGridItem';

import ViewItem = api.app.view.ViewItem;
import ItemStatisticsPanel = api.app.view.ItemStatisticsPanel;
import ItemDataGroup = api.app.view.ItemDataGroup;

import Principal = api.security.Principal;
import PrincipalKey = api.security.PrincipalKey;
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

            this.appendMetadata(item);

            super.setItem(item);
        }
    }

    private populatePrincipalViewItem(item: ViewItem<UserTreeGridItem>) {
        item.setPathName(item.getModel().getPrincipal().getKey().getId());
        item.setPath(item.getModel().getPrincipal().getKey().toPath(true));
        item.setIconSize(128);
    }

    private appendMetadata(item: ViewItem<UserTreeGridItem>) {
        const principal = item.getModel().getPrincipal();
        const type = principal ? principal.getTypeName().toLowerCase() : '';

        if (type) {
            const mainGroup = new ItemDataGroup(i18n(`field.${type}`), type);
            let metaGroups: wemQ.Promise<ItemDataGroup[]>;

            switch (principal.getType()) {
            case PrincipalType.USER:
                metaGroups = this.createUserMetadataGroups(principal, mainGroup);
                break;
            case PrincipalType.GROUP:
                metaGroups = this.createGroupOrRoleMetadataGroups(principal, mainGroup);
                break;
            case PrincipalType.ROLE:
                metaGroups = this.createGroupOrRoleMetadataGroups(principal, mainGroup);
                break;
            }

            metaGroups.then((groups: ItemDataGroup[]) => {
                this.userDataContainer.removeChildren();
                this.userDataContainer.appendChildren(...groups);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }
    }

    private createPrincipalViewer(principal: Principal): PrincipalViewer {
        const viewer = new PrincipalViewer();
        viewer.setObject(principal);
        return viewer;
    }

    private createUserMetadataGroups(principal: Principal, mainGroup: ItemDataGroup): wemQ.Promise<ItemDataGroup[]> {
        this.userDataContainer.appendChild(mainGroup);

        const rolesAndGroupsGroup = new ItemDataGroup(i18n('field.rolesAndGroups'), 'memberships');
        this.userDataContainer.appendChild(rolesAndGroupsGroup);

        return new GetPrincipalByKeyRequest(principal.getKey()).setIncludeMemberships(true).sendAndParse().then((p: Principal) => {
            const user = p.asUser();
            mainGroup.addDataList(i18n('field.email'), user.getEmail());

            const roles = user.getMemberships().filter(el => el.isRole()).map(el => this.createPrincipalViewer(el));
            rolesAndGroupsGroup.addDataElements(i18n('field.roles'), roles);

            let groups = p.asUser().getMemberships().filter(el => el.isGroup()).map(el => this.createPrincipalViewer(el));
            rolesAndGroupsGroup.addDataElements(i18n('field.groups'), groups);

            return [mainGroup, rolesAndGroupsGroup];
        });
    }

    createGroupOrRoleMetadataGroups(principal: Principal, mainGroup: ItemDataGroup): wemQ.Promise<ItemDataGroup[]> {
        mainGroup.appendChild(new api.dom.DivEl('description').setHtml(principal.getDescription()));
        this.userDataContainer.appendChild(mainGroup);

        let rolesGroup;
        if (principal.isGroup()) {
            rolesGroup = new ItemDataGroup(i18n('field.roles'), 'roles');
            this.userDataContainer.appendChild(rolesGroup);
        }

        let membersGroup;
        membersGroup = new ItemDataGroup(i18n('field.members'), 'members');
        this.userDataContainer.appendChild(membersGroup);

        return new GetPrincipalByKeyRequest(principal.getKey()).setIncludeMemberships(true).sendAndParse().then((p: Principal) => {
            const group = principal.isGroup() ? p.asGroup() : p.asRole();

            if (principal.isGroup()) {
                rolesGroup.addDataElements(null, p.asGroup().getMemberships().map(el => this.createPrincipalViewer(el)));
            }

            const membersPromises = group.getMembers().map(el => new GetPrincipalByKeyRequest(el).sendAndParse());

            return wemQ.all(membersPromises).then((results: Principal[]) => {
                membersGroup.addDataElements(null, results.map(el => this.createPrincipalViewer(el)));
            }).then(() => (principal.isGroup() ? [mainGroup, rolesGroup, membersGroup] : [mainGroup, membersGroup] ));
        });
    }
}
