module app {

    import AppBarTabMenuItem = api.app.bar.AppBarTabMenuItem;
    import AppBarTabMenuItemBuilder = api.app.bar.AppBarTabMenuItemBuilder;
    import AppBarTabId = api.app.bar.AppBarTabId;
    import UserTreeGridItem = app.browse.UserTreeGridItem;
    import UserItemsTreeGrid = app.browse.UserItemsTreeGrid;
    import UserTreeGridItemType = app.browse.UserTreeGridItemType;
    import PrincipalType = api.security.PrincipalType;
    import PrincipalKey = api.security.PrincipalKey;
    import UserStore = api.security.UserStore;
    import GetUserStoreByKeyRequest = api.security.GetUserStoreByKeyRequest;
    import UserItemWizardPanel = app.wizard.UserItemWizardPanel;

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
                    new api.security.GetPrincipalByKeyRequest(api.security.PrincipalKey.fromString(id)).sendAndParse().
                        done((principal: api.security.Principal) => {
                            new app.browse.EditPrincipalEvent([
                                new app.browse.UserTreeGridItemBuilder().
                                    setPrincipal(principal).
                                    setType(UserTreeGridItemType.PRINCIPAL).
                                    build()
                            ]).fire();
                        });
                } else {
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

        addWizardPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<any>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() === "displayName") {
                    tabMenuItem.setLabel(<string>event.getNewValue());
                }
            });
        }

        private handleGlobalEvents() {

            api.app.ShowBrowsePanelEvent.on((event) => {
                this.handleBrowse(event);
            });

            app.browse.NewPrincipalEvent.on((event) => {
                this.handleNew(event);
            });

            app.browse.EditPrincipalEvent.on((event) => {
                this.handleEdit(event);
            });
        }

        private handleBrowse(event: api.app.ShowBrowsePanelEvent) {
            var browsePanel: api.app.browse.BrowsePanel<UserTreeGridItem> = this.getBrowsePanel();
            if (!browsePanel) {
                this.addBrowsePanel(new app.browse.UserBrowsePanel());
            } else {
                this.selectPanelByIndex(this.getPanelIndex(browsePanel));
            }
        }


        private handleWizardCreated(wizard: UserItemWizardPanel<api.Equitable>, tabName: string) {
            var tabMenuItem = new AppBarTabMenuItemBuilder().setLabel("[New " + tabName + "]").
                setTabId(wizard.getTabId()).
                setCloseAction(wizard.getCloseAction()).
                build();


            this.addWizardPanel(tabMenuItem, wizard);

        }

        private handleWizardUpdated(wizard: UserItemWizardPanel<api.Equitable>, tabMenuItem: AppBarTabMenuItem,
                                    closeMenuItem: AppBarTabMenuItem) {

            var displayName, id: string;
            if (api.ObjectHelper.iFrameSafeInstanceOf(wizard.getPersistedItem(), api.security.Principal)) {
                displayName = (<api.security.Principal>wizard.getPersistedItem()).getDisplayName();
                id = (<api.security.Principal>wizard.getPersistedItem()).getKey().getId();
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(wizard.getPersistedItem(), api.security.UserStore)) {
                displayName = (<api.security.UserStore>wizard.getPersistedItem()).getDisplayName()
                id = (<api.security.UserStore>wizard.getPersistedItem()).getKey().getId();
            }

            if (closeMenuItem != null) {
                this.getAppBarTabMenu().deselectNavigationItem();
                this.getAppBarTabMenu().removeNavigationItem(closeMenuItem);
                this.removePanelByIndex(closeMenuItem.getIndex());
            }
            tabMenuItem = new AppBarTabMenuItemBuilder().setLabel(displayName).
                setTabId(wizard.getTabId()).
                setEditing(true).
                setCloseAction(wizard.getCloseAction()).
                build();
            this.addWizardPanel(tabMenuItem, wizard);

            var viewTabId = AppBarTabId.forView(id);
            var viewTabMenuItem = this.getAppBarTabMenu().getNavigationItemById(viewTabId);
            if (viewTabMenuItem != null) {
                this.removePanelByIndex(viewTabMenuItem.getIndex());
            }

        }

        private handleNew(event: app.browse.NewPrincipalEvent) {
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
                    break;
                case UserTreeGridItemType.GROUPS:
                    principalType = PrincipalType.GROUP;
                    principalPath = PrincipalKey.ofGroup(userItem.getUserStore().getKey(), "none").toPath(true);
                    tabName = "Group";
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
                    tabName = tabName[0] + tabName.slice(0).toUpperCase();
                    // Roles does not have a userstore link
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
                        return new app.wizard.PrincipalWizardPanelFactory().
                            setAppBarTabId(tabId).
                            setPrincipalType(principalType).
                            setPrincipalPath(principalPath).
                            setUserStore(userStore).
                            setParentOfSameType(userItem.getType() === UserTreeGridItemType.PRINCIPAL).
                            createForNew();
                    }).then((wizard: app.wizard.PrincipalWizardPanel) => {
                        this.handleWizardCreated(wizard, tabName);
                        wizard.onPrincipalNamed((event: api.security.PrincipalNamedEvent) => {
                            this.handlePrincipalNamedEvent(event);
                        });

                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).finally(() => {
                        this.mask.hide();
                    }).done();
                } else {
                    new app.wizard.UserStoreWizardPanelFactory().
                        setAppBarTabId(tabId).createForNew().then((wizard: app.wizard.UserStoreWizardPanel) => {

                            this.handleWizardCreated(wizard, tabName);

                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                            this.mask.hide();
                        }).done();
                }
            }
        }

        private handleEdit(event: app.browse.EditPrincipalEvent) {
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
                        new app.wizard.PrincipalWizardPanelFactory().
                            setAppBarTabId(tabId).
                            setPrincipalType(userItem.getPrincipal().getType()).
                            setPrincipalPath(userItem.getPrincipal().getKey().toPath(true)).
                            setPrincipalToEdit(userItem.getPrincipal().getKey()).
                            createForEdit().then((wizard: app.wizard.PrincipalWizardPanel) => {

                                this.handleWizardUpdated(wizard, tabMenuItem, closeViewPanelMenuItem);

                            }).catch((reason: any) => {
                                api.DefaultErrorHandler.handle(reason);
                            }).finally(() => {
                                this.mask.hide();
                            }).done();
                    } else if (userItem.getType() === UserTreeGridItemType.USER_STORE) {

                        new app.wizard.UserStoreWizardPanelFactory().
                            setAppBarTabId(tabId).
                            setUserStoreKey(userItem.getUserStore().getKey()).
                            createForEdit().then((wizard: app.wizard.UserStoreWizardPanel) => {

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

        private handlePrincipalNamedEvent(eventt: api.event.Event) {
            var event = <api.security.PrincipalNamedEvent>eventt;
            var wizard = event.getWizard(),
                tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(wizard.getTabId());
            // update tab id so that new wizard for the same content type can be created
            var newTabId = api.app.bar.AppBarTabId.forEdit(event.getPrincipal().getKey().getId());
            tabMenuItem.setTabId(newTabId);
            wizard.setTabId(newTabId);
            this.getAppBarTabMenu().getNavigationItemById(newTabId).setLabel(event.getPrincipal().getDisplayName());
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

    }
}