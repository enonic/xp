module app {

    import AppBarTabMenuItem = api.app.bar.AppBarTabMenuItem;
    import AppBarTabMenuItemBuilder = api.app.bar.AppBarTabMenuItemBuilder;
    import AppBarTabId = api.app.bar.AppBarTabId;
    import UserTreeGridItem = app.browse.UserTreeGridItem;

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
                                    build()
                            ]).fire();
                        });
                }
                break;
            case 'view' :
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

        private handleGlobalEvents() {

            api.app.bar.event.ShowBrowsePanelEvent.on((event) => {
                this.handleBrowse(event);
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
                this.showPanel(browsePanel);
            }
        }

        private handleEdit(event: app.browse.EditPrincipalEvent) {

            var userItems: UserTreeGridItem[] = event.getPrincipals();

            userItems.forEach((userItem: UserTreeGridItem) => {
                if (!userItem) {
                    return;
                }

                var closeViewPanelMenuItem = this.resolveTabMenuItemForContentBeingViewed(userItem);
                var tabMenuItem = this.resolveTabMenuItemForContentBeingEdited(userItem);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);
                } else {
                    this.mask.show();
                    var tabId = AppBarTabId.forEdit(userItem.getPrincipal().getKey().getId());

                    new app.wizard.PrincipalWizardPanelFactory().
                        setAppBarTabId(tabId).
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

        private resolveTabMenuItemForContentBeingEditedOrViewed(userItem: UserTreeGridItem): AppBarTabMenuItem {
            var result = this.resolveTabMenuItemForContentBeingEdited(userItem);
            if (!result) {
                result = this.resolveTabMenuItemForContentBeingViewed(userItem)
            }
            return result;
        }

        private resolveTabMenuItemForContentBeingEdited(userItem: UserTreeGridItem): AppBarTabMenuItem {
            if (!!userItem) {

                var tabId = this.getAppBarTabMenu().getNavigationItemById(AppBarTabId.forEdit(userItem.getPrincipal().getKey().getId()));
                if (tabId) {
                    return tabId;
                }
            }
            return null;
        }

        private resolveTabMenuItemForContentBeingViewed(userItem: UserTreeGridItem): AppBarTabMenuItem {
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