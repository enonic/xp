module app {

    import AppBarTabMenuItem = api.app.bar.AppBarTabMenuItem;
    import AppBarTabMenuItemBuilder = api.app.bar.AppBarTabMenuItemBuilder;
    import AppBarTabId = api.app.bar.AppBarTabId;
    import UserTreeGridItem = app.browse.UserTreeGridItem;
    import UserItemsTreeGrid = app.browse.UserItemsTreeGrid;
    import UserTreeGridItemType = app.browse.UserTreeGridItemType;
    import PrincipalType = api.security.PrincipalType;

    export class UserAppPanel extends api.app.BrowseAndWizardBasedAppPanel<UserTreeGridItem> {

        private mask: api.ui.mask.LoadMask;

        constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

            super({
                appBar: appBar
            });

            this.mask = new api.ui.mask.LoadMask(this);

            this.handleGlobalEvents();

            this.route(path)
        }

        private route(path?: api.rest.Path) {
            var action = path ? path.getElement(0) : undefined;
            switch (action) {
            case 'edit':
                var id = path.getElement(1);
                if (id) {
                    new api.security.GetPrincipalByKeyRequest(api.security.PrincipalKey.fromString(id)).sendAndParse().
                        done((principal: api.security.Principal) => {
                            new app.browse.EditPrincipalEvent([
                                new app.browse.UserTreeGridItemBuilder().
                                    setPrincipal(principal).
                                    setType(UserTreeGridItemType.PRINCIPAL).
                                    build()
                            ]).fire();
                        });
                }
                break;
            case 'view':
                var id = path.getElement(1);
                if (id) {
                    //TODO
                }
                break;
            default:
                new api.app.bar.event.ShowBrowsePanelEvent().fire();
                break;
            }
        }

        addWizardPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<api.security.Principal>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == "displayName") {
                    tabMenuItem.setLabel(<string>event.getNewValue());
                }
            });
        }

        private handleGlobalEvents() {

            api.app.bar.event.ShowBrowsePanelEvent.on((event) => {
                this.handleBrowse(event);
            });

            app.browse.NewPrincipalEvent.on((event) => {
                this.handleNew(event);
            });

            app.browse.EditPrincipalEvent.on((event) => {
                this.handleEdit(event);
            });
        }

        private handleBrowse(event: api.app.bar.event.ShowBrowsePanelEvent) {
            var browsePanel: api.app.browse.BrowsePanel<UserTreeGridItem> = this.getBrowsePanel();
            if (!browsePanel) {
                this.addBrowsePanel(new app.browse.UserBrowsePanel());
            } else {
                this.selectPanelByIndex(this.getPanelIndex(browsePanel));
            }
        }

        private handleNew(event: app.browse.NewPrincipalEvent) {
            var userItem: UserTreeGridItem = event.getPrincipals()[0],
                principalType,
                tabName;

            if (userItem) {
                switch (userItem.getType()) {
                    case UserTreeGridItemType.USERS:
                        /* TODO: Add assign, when the UserBrowsePanel in implemented. */
                        principalType = PrincipalType.USER;
                        break;
                    case UserTreeGridItemType.GROUPS:
                        principalType = PrincipalType.GROUP;
                        tabName = "Group";
                        break;
                    case UserTreeGridItemType.ROLES:
                        principalType = PrincipalType.ROLE;
                        tabName = "Role";
                        break;
                }
            }

            var tabId = !!tabName ? AppBarTabId.forNew(tabName) : null,
                tabMenuItem = !!tabId ? this.getAppBarTabMenu().getNavigationItemById(tabId) : null;

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);
            } else {
                this.mask.show();
                new app.wizard.PrincipalWizardPanelFactory().
                    setAppBarTabId(tabId).
                    setPrincipalType(principalType).
                    setPrincipalPath(userItem.getDataPath()).
                    setUserStore(userItem.getUserStore() ? userItem.getUserStore().getKey() : null).
                    createForNew().then((wizard: app.wizard.PrincipalWizardPanel) => {
                        tabMenuItem = new AppBarTabMenuItemBuilder().setLabel("[New " + tabName + "]").
                            setTabId(tabId).
                            setCloseAction(wizard.getCloseAction()).
                            build();

                        wizard.onPrincipalNamed((event: api.security.PrincipalNamedEvent) => {
                            this.handlePrincipalNamedEvent(event);
                        });

                        this.addWizardPanel(tabMenuItem, wizard);

                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).finally(() => {
                        this.mask.hide();
                    }).done();
            }
        }

        private handleEdit(event: app.browse.EditPrincipalEvent) {
            var userItems: UserTreeGridItem[] = event.getPrincipals();

            userItems.forEach((userItem: UserTreeGridItem) => {
                if (!userItem || userItem.getType() !== UserTreeGridItemType.PRINCIPAL) {
                    return;
                }
                var closeViewPanelMenuItem = this.resolveTabMenuItemForPrincipalBeingViewed(userItem);
                var tabMenuItem = this.resolveTabMenuItemForPrincipalBeingEdited(userItem);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);
                } else {
                    this.mask.show();
                    var tabId = AppBarTabId.forEdit(userItem.getPrincipal().getKey().getId());

                    new app.wizard.PrincipalWizardPanelFactory().
                        setAppBarTabId(tabId).
                        setPrincipalType(userItem.getPrincipal().getType()).
                        setPrincipalPath(userItem.getDataPath()).
                        setPrincipalToEdit(userItem.getPrincipal().getKey()).
                        createForEdit().then((wizard: app.wizard.PrincipalWizardPanel) => {
                            if (closeViewPanelMenuItem != null) {
                                this.getAppBarTabMenu().deselectNavigationItem();
                                this.getAppBarTabMenu().removeNavigationItem(closeViewPanelMenuItem);
                                this.removePanelByIndex(closeViewPanelMenuItem.getIndex());
                            }
                            tabMenuItem = new AppBarTabMenuItemBuilder().setLabel(userItem.getPrincipal().getDisplayName()).
                                setTabId(tabId).
                                setEditing(true).
                                setCloseAction(wizard.getCloseAction()).
                                build();
                            this.addWizardPanel(tabMenuItem, wizard);

                            var viewTabId = AppBarTabId.forView(userItem.getPrincipal().getKey().getId());
                            var viewTabMenuItem = this.getAppBarTabMenu().getNavigationItemById(viewTabId);
                            if (viewTabMenuItem != null) {
                                this.removePanelByIndex(viewTabMenuItem.getIndex());
                            }
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                            this.mask.hide();
                        }).done();
                }
            });
        }

        private handlePrincipalNamedEvent(event: api.security.PrincipalNamedEvent) {

            var wizard = event.getWizard(),
                tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(wizard.getTabId());
            // update tab id so that new wizard for the same content type can be created
            var newTabId = api.app.bar.AppBarTabId.forEdit(event.getPrincipal().getKey().getId());
            tabMenuItem.setTabId(newTabId);
            wizard.setTabId(newTabId);
            this.getAppBarTabMenu().getNavigationItemById(newTabId).setLabel(event.getPrincipal().getDisplayName());
        }

        private resolveTabMenuItemForPrincipalBeingEditedOrViewed(userItem: UserTreeGridItem): AppBarTabMenuItem {
            var result = this.resolveTabMenuItemForPrincipalBeingEdited(userItem);
            if (!result) {
                result = this.resolveTabMenuItemForPrincipalBeingViewed(userItem)
            }
            return result;
        }

        private resolveTabMenuItemForPrincipalBeingEdited(userItem: UserTreeGridItem): AppBarTabMenuItem {
            if (!!userItem) {

                var tabId = this.getAppBarTabMenu().getNavigationItemById(AppBarTabId.forEdit(userItem.getPrincipal().getKey().getId()));
                if (tabId) {
                    return tabId;
                }
            }
            return null;
        }

        private resolveTabMenuItemForPrincipalBeingViewed(userItem: UserTreeGridItem): AppBarTabMenuItem {
            if (!!userItem) {
                var tabId = this.getAppBarTabMenu().getNavigationItemById(AppBarTabId.forView(userItem.getPrincipal().getKey().getId()));
                if (tabId) {
                    return tabId;
                }
            }

            return null;
        }
    }
}