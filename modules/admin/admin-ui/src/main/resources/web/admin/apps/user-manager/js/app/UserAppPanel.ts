import "../api.ts";
import {UserTreeGridItem, UserTreeGridItemType, UserTreeGridItemBuilder} from "./browse/UserTreeGridItem";
import {UserItemWizardPanel} from "./wizard/UserItemWizardPanel";
import {UserStoreWizardPanel} from "./wizard/UserStoreWizardPanel";
import {PrincipalWizardPanel} from "./wizard/PrincipalWizardPanel";
import {PrincipalWizardPanelFactory} from "./wizard/PrincipalWizardPanelFactory";
import {UserStoreWizardPanelFactory} from "./wizard/UserStoreWizardPanelFactory";
import {NewPrincipalEvent} from "./browse/NewPrincipalEvent";
import {EditPrincipalEvent} from "./browse/EditPrincipalEvent";
import {UserBrowsePanel} from "./browse/UserBrowsePanel";

import AppBarTabMenuItem = api.app.bar.AppBarTabMenuItem;
import AppBarTabMenuItemBuilder = api.app.bar.AppBarTabMenuItemBuilder;
import AppBarTabId = api.app.bar.AppBarTabId;
import Principal = api.security.Principal;
import PrincipalType = api.security.PrincipalType;
import PrincipalKey = api.security.PrincipalKey;
import UserStore = api.security.UserStore;
import GetUserStoreByKeyRequest = api.security.GetUserStoreByKeyRequest;

export class UserAppPanel extends api.app.BrowseAndWizardBasedAppPanel<UserTreeGridItem> {

    private mask: api.ui.mask.LoadMask;

    constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

        super({
            appBar: appBar
        });
        this.mask = new api.ui.mask.LoadMask(this);

        this.handleGlobalEvents();

        this.route(path);
    }

    private route(path?: api.rest.Path) {
        var action = path ? path.getElement(0) : undefined;
        switch (action) {
        case 'edit':
            var id = path.getElement(1);
            if (id && this.isValidPrincipalKey(id)) {
                new api.security.GetPrincipalByKeyRequest(api.security.PrincipalKey.fromString(id)).sendAndParse().done(
                    (principal: api.security.Principal) => {
                        new EditPrincipalEvent([
                            new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build()
                        ]).fire();
                    });
            } else if (id && this.isValidUserStoreKey(id)) {
                new GetUserStoreByKeyRequest(api.security.UserStoreKey.fromString(id)).sendAndParse().done((userStore: UserStore) => {
                    new EditPrincipalEvent([
                        new UserTreeGridItemBuilder().setUserStore(userStore).setType(
                            UserTreeGridItemType.USER_STORE).build()
                    ]).fire();
                });
            }
            else {
                new api.app.ShowBrowsePanelEvent().fire();
            }
            break;
        case 'view':
            var id = path.getElement(1);
            if (id) {
                //TODO
            }
            break;
        default:
            new api.app.ShowBrowsePanelEvent().fire();
            break;
        }
    }

    private isValidPrincipalKey(value: string): boolean {
        try {
            api.security.PrincipalKey.fromString(value);
            return true;
        } catch (e) {
            return false;
        }
    }

    private isValidUserStoreKey(value: string): boolean {
        try {
            api.security.UserStoreKey.fromString(value);
            return true;
        } catch (e) {
            return false;
        }
    }

    addWizardPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<any>) {
        super.addWizardPanel(tabMenuItem, wizardPanel);

        wizardPanel.getHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
            if (event.getPropertyName() === "displayName") {
                var name = <string>event.getNewValue();
                if (api.ObjectHelper.iFrameSafeInstanceOf(wizardPanel, UserStoreWizardPanel)) {
                    name = name ||
                           api.content.ContentUnnamed.prettifyUnnamed((<UserStoreWizardPanel>wizardPanel).getUserItemType());
                } else if (api.ObjectHelper.iFrameSafeInstanceOf(wizardPanel, PrincipalWizardPanel)) {
                    name = name ||
                           api.content.ContentUnnamed.prettifyUnnamed((<PrincipalWizardPanel>wizardPanel).getUserItemType());
                }
                tabMenuItem.setLabel(name, !<string>event.getNewValue());
            }
        });

        //tabMenuItem.markInvalid(!wizardPanel.getPersistedItem().isValid());

        wizardPanel.onValidityChanged((event: api.ValidityChangedEvent) => {
            tabMenuItem.markInvalid(!wizardPanel.isValid());
        });
    }

    private handleGlobalEvents() {

        api.app.ShowBrowsePanelEvent.on((event) => {
            this.handleBrowse(event);
        });

        NewPrincipalEvent.on((event) => {
            this.handleNew(event);
        });

        EditPrincipalEvent.on((event) => {
            this.handleEdit(event);
        });
    }

    private handleBrowse(event: api.app.ShowBrowsePanelEvent) {
        var browsePanel: api.app.browse.BrowsePanel<UserTreeGridItem> = this.getBrowsePanel();
        if (!browsePanel) {
            this.addBrowsePanel(new UserBrowsePanel());
        } else {
            this.selectPanelByIndex(this.getPanelIndex(browsePanel));
        }
    }


    private handleWizardCreated(wizard: UserItemWizardPanel<api.Equitable>, tabName: string) {
        var tabMenuItem = new AppBarTabMenuItemBuilder().setLabel(api.content.ContentUnnamed.prettifyUnnamed(tabName)).setTabId(
            wizard.getTabId()).setCloseAction(wizard.getCloseAction()).build();


        this.addWizardPanel(tabMenuItem, wizard);

    }

    private handleWizardUpdated(wizard: UserItemWizardPanel<api.Equitable>, tabMenuItem: AppBarTabMenuItem,
                                closeMenuItem: AppBarTabMenuItem) {

        var displayName, id: string;
        if (api.ObjectHelper.iFrameSafeInstanceOf(wizard.getPersistedItem(), api.security.Principal)) {
            displayName = (<api.security.Principal>wizard.getPersistedItem()).getDisplayName() ||
                          api.content.ContentUnnamed.prettifyUnnamed((<api.security.Principal>wizard.getPersistedItem()).getDisplayName());
            id = (<api.security.Principal>wizard.getPersistedItem()).getKey().getId();
        } else if (api.ObjectHelper.iFrameSafeInstanceOf(wizard.getPersistedItem(), api.security.UserStore)) {
            displayName = (<api.security.UserStore>wizard.getPersistedItem()).getDisplayName() ||
                          api.content.ContentUnnamed.prettifyUnnamed((<UserStoreWizardPanel>wizard).getUserItemType());
            id = (<api.security.UserStore>wizard.getPersistedItem()).getKey().getId();
        }

        if (closeMenuItem != null) {
            this.getAppBarTabMenu().deselectNavigationItem();
            this.getAppBarTabMenu().removeNavigationItem(closeMenuItem);
            this.removePanelByIndex(closeMenuItem.getIndex());
        }
        tabMenuItem = new AppBarTabMenuItemBuilder().setLabel(displayName).setTabId(wizard.getTabId()).setEditing(true).setCloseAction(
            wizard.getCloseAction()).build();
        this.addWizardPanel(tabMenuItem, wizard);

        var viewTabId = AppBarTabId.forView(id);
        var viewTabMenuItem = this.getAppBarTabMenu().getNavigationItemById(viewTabId);
        if (viewTabMenuItem != null) {
            this.removePanelByIndex(viewTabMenuItem.getIndex());
        }

    }

    private handleNew(event: NewPrincipalEvent) {
        var userItem: UserTreeGridItem = event.getPrincipals()[0],
            userStoreDeferred = wemQ.defer<UserStore>(),
            userStoreRequest,
            principalType,
            principalPath = "",
            tabName;

        if (userItem) {
            userStoreDeferred.resolve(userItem.getUserStore());
            userStoreRequest = userStoreDeferred.promise;

            switch (userItem.getType()) {
            case UserTreeGridItemType.USERS:
                principalType = PrincipalType.USER;
                principalPath = PrincipalKey.ofUser(userItem.getUserStore().getKey(), "none").toPath(true);
                tabName = "User";
                userStoreRequest = new GetUserStoreByKeyRequest(userItem.getUserStore().getKey()).sendAndParse();
                break;
            case UserTreeGridItemType.GROUPS:
                principalType = PrincipalType.GROUP;
                principalPath = PrincipalKey.ofGroup(userItem.getUserStore().getKey(), "none").toPath(true);
                tabName = "Group";
                userStoreRequest = new GetUserStoreByKeyRequest(userItem.getUserStore().getKey()).sendAndParse();
                break;
            case UserTreeGridItemType.ROLES:
                principalType = PrincipalType.ROLE;
                principalPath = PrincipalKey.ofRole("none").toPath(true);
                tabName = "Role";
                break;
            case UserTreeGridItemType.PRINCIPAL:
                principalType = userItem.getPrincipal().getType();
                principalPath = userItem.getPrincipal().getKey().toPath(true);
                tabName = PrincipalType[principalType];
                tabName = tabName[0] + tabName.slice(1).toLowerCase();
                // Roles does not have a UserStore link
                if (userItem.getPrincipal().getType() !== PrincipalType.ROLE) {
                    userStoreRequest = new GetUserStoreByKeyRequest(userItem.getPrincipal().getKey().getUserStore()).sendAndParse();
                }
                break;
            case UserTreeGridItemType.USER_STORE:
                tabName = "User Store";
            }
        } else {
            tabName = "User Store";
        }

        var tabId = !!tabName ? AppBarTabId.forNew(tabName) : null,
            tabMenuItem = !!tabId ? this.getAppBarTabMenu().getNavigationItemById(tabId) : null;

        if (tabMenuItem != null) {
            this.selectPanel(tabMenuItem);
        } else {
            this.mask.show();
            if (userItem && userItem.getType() !== UserTreeGridItemType.USER_STORE) {

                userStoreRequest.then((userStore: UserStore) => {
                    if (principalType === PrincipalType.USER && !this.areUsersEditable(userStore)) {
                        api.notify.showError("The ID Provider selected for this user store does not allow to create users.");
                        return;
                    }
                    if (principalType === PrincipalType.GROUP && !this.areGroupsEditable(userStore)) {
                        api.notify.showError("The ID Provider selected for this user store does not allow to create groups.");
                        return;
                    }
                    return new PrincipalWizardPanelFactory().setAppBarTabId(tabId).setPrincipalType(
                        principalType).setPrincipalPath(principalPath).setUserStore(userStore).setParentOfSameType(
                        userItem.getType() === UserTreeGridItemType.PRINCIPAL).createForNew();
                }).then((wizard: PrincipalWizardPanel) => {
                    if (wizard) {
                        this.handleWizardCreated(wizard, tabName);
                        wizard.onPrincipalNamed((event: api.security.PrincipalNamedEvent) => {
                            this.handlePrincipalNamedEvent(event);
                        });
                    }

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.mask.hide();
                }).done();
            } else {
                new UserStoreWizardPanelFactory().setAppBarTabId(tabId).createForNew().then(
                    (wizard: UserStoreWizardPanel) => {

                        this.handleWizardCreated(wizard, tabName);

                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).finally(() => {
                        this.mask.hide();
                    }).done();
            }
        }
    }

    private handleEdit(event: EditPrincipalEvent) {
        var userItems: UserTreeGridItem[] = event.getPrincipals();

        userItems.forEach((userItem: UserTreeGridItem) => {
            if (!userItem) {
                return;
            }
            var closeViewPanelMenuItem = this.resolveTabMenuItemForUserItemBeingViewed(userItem);
            var tabMenuItem = this.resolveTabMenuItemForUserItemBeingEdited(userItem);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);
            } else {
                this.mask.show();
                var tabId = this.getTabIdForUserItem(userItem);

                if (userItem.getType() == UserTreeGridItemType.PRINCIPAL) {
                    this.handlePrincipalEdit(userItem.getPrincipal(), tabId, tabMenuItem, closeViewPanelMenuItem);
                } else if (userItem.getType() === UserTreeGridItemType.USER_STORE) {

                    new UserStoreWizardPanelFactory().setAppBarTabId(tabId).setUserStoreKey(
                        userItem.getUserStore().getKey()).createForEdit().then((wizard: UserStoreWizardPanel) => {

                            this.handleWizardUpdated(wizard, tabMenuItem, closeViewPanelMenuItem);

                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                            this.mask.hide();
                        }).done();
                }
            }
        });
    }

    private handlePrincipalEdit(principal: Principal, tabId: AppBarTabId, tabMenuItem: AppBarTabMenuItem,
                                closeViewPanelMenuItem: AppBarTabMenuItem) {
        var principalType = principal.getType();
        if (PrincipalType.USER == principalType || PrincipalType.GROUP == principalType) {
            var userStoreKey = principal.getKey().getUserStore();
            new GetUserStoreByKeyRequest(userStoreKey).
                sendAndParse().then((userStore: UserStore) => {
                    if (PrincipalType.USER == principalType && !this.areUsersEditable(userStore)) {
                        api.notify.showError("The ID Provider selected for this user store does not allow to edit users.");
                        return;
                    } else if (PrincipalType.GROUP == principalType && !this.areGroupsEditable(userStore)) {
                        api.notify.showError("The ID Provider selected for this user store does not allow to edit groups.");
                        return;
                    } else {
                        this.createPrincipalWizardPanelForEdit(principal, tabId, tabMenuItem, closeViewPanelMenuItem);
                    }
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.mask.hide();
                }).done();
        } else {
            this.createPrincipalWizardPanelForEdit(principal, tabId, tabMenuItem, closeViewPanelMenuItem);
        }
    }

    private createPrincipalWizardPanelForEdit(principal: Principal, tabId: AppBarTabId, tabMenuItem: AppBarTabMenuItem,
                                              closeViewPanelMenuItem: AppBarTabMenuItem) {
        new PrincipalWizardPanelFactory().
            setAppBarTabId(tabId).
            setPrincipalType(principal.getType()).
            setPrincipalPath(principal.getKey().toPath(true)).
            setPrincipalToEdit(principal.getKey()).
            createForEdit().
            then((wizard: PrincipalWizardPanel) => {

                this.handleWizardUpdated(wizard, tabMenuItem, closeViewPanelMenuItem);

            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).finally(() => {
                this.mask.hide();
            }).done();
    }

    private handlePrincipalNamedEvent(event: api.event.Event) {
        var e = <api.security.PrincipalNamedEvent>event;
        var wizard = e.getWizard(),
            tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(wizard.getTabId());
        // update tab id so that new wizard for the same content type can be created
        var newTabId = api.app.bar.AppBarTabId.forEdit(e.getPrincipal().getKey().getId());
        tabMenuItem.setTabId(newTabId);
        wizard.setTabId(newTabId);

        var name = e.getPrincipal().getDisplayName();
        if (api.ObjectHelper.iFrameSafeInstanceOf(wizard, PrincipalWizardPanel)) {
            name = name ||
                   api.content.ContentUnnamed.prettifyUnnamed((<UserItemWizardPanel<Principal>>wizard).getUserItemType());
        }
        this.getAppBarTabMenu().getNavigationItemById(newTabId).setLabel(name, !e.getPrincipal().getDisplayName());
    }

    private resolveTabMenuItemForUserItemBeingEditedOrViewed(userItem: UserTreeGridItem): AppBarTabMenuItem {
        var result = this.resolveTabMenuItemForUserItemBeingEdited(userItem);
        if (!result) {
            result = this.resolveTabMenuItemForUserItemBeingViewed(userItem)
        }
        return result;
    }

    private resolveTabMenuItemForUserItemBeingEdited(userItem: UserTreeGridItem): AppBarTabMenuItem {
        if (!!userItem) {
            var appBarTabId: AppBarTabId = this.getTabIdForUserItem(userItem);
            var tabId = this.getAppBarTabMenu().getNavigationItemById(appBarTabId);
            if (tabId) {
                return tabId;
            }
        }
        return null;
    }

    private resolveTabMenuItemForUserItemBeingViewed(userItem: UserTreeGridItem): AppBarTabMenuItem {
        if (!!userItem) {
            var appBarTabId: AppBarTabId = this.getTabIdForUserItem(userItem);
            var tabId = this.getAppBarTabMenu().getNavigationItemById(appBarTabId);
            if (tabId) {
                return tabId;
            }
        }

        return null;
    }

    private getTabIdForUserItem(userItem: UserTreeGridItem): AppBarTabId {
        var appBarTabId: AppBarTabId;
        if (UserTreeGridItemType.PRINCIPAL == userItem.getType()) {
            appBarTabId = AppBarTabId.forEdit(userItem.getPrincipal().getKey().getId());
        } else if (UserTreeGridItemType.USER_STORE == userItem.getType()) {
            appBarTabId = AppBarTabId.forEdit(userItem.getUserStore().getKey().getId());
        }
        return appBarTabId;
    }

    private areUsersEditable(userStore: UserStore): boolean {
        var idProviderMode = userStore.getIdProviderMode();
        return api.security.IdProviderMode.EXTERNAL != idProviderMode && api.security.IdProviderMode.MIXED != idProviderMode;
    }

    private areGroupsEditable(userStore: UserStore): boolean {
        var idProviderMode = userStore.getIdProviderMode();
        return api.security.IdProviderMode.EXTERNAL != idProviderMode;
    }

}
