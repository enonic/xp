module admin.ui {

    export class TopBar {
        ext;
        private startButton:any; // Ext.button.Button
        private homeButton:any; // Ext.button.Button
        private leftContainer:any; // Ext.container.Container
        private rightContainer:any; // Ext.container.Container
        private tabPanel:any; // Admin.view.TabPanel?
        private tabMenu:admin.ui.TopBarMenu;
        private titleButton:any; // Ext.button.Button
        private appName:string;


        constructor(appName:string, tabPanel?:any) {
            var tb = new Ext.toolbar.Toolbar({});
            this.ext = tb;
            tb.itemId = 'topBar';
            tb.buttonAlign = 'center';
            tb.addCls('admin-topbar-panel')
            tb.dock = 'top';
            tb.plain = true;
            tb.border = false;
            this.appName = appName;
            this.tabPanel = tabPanel;

            this.initComponent();
        }

        initComponent():void {
            var me = this.ext;

            this.startButton = Ext.create('Ext.button.Button', {
                xtype: 'button',
                itemId: 'app-launcher-button',
                margins: '0 8px 0 0',
                cls: 'start-button',
                handler: (btn, evt) => {
                    this.toggleHomeScreen();
                }
            });

            this.homeButton = Ext.create('Ext.button.Button', {
                text: this.appName || '&lt; app name &gt;',
                cls: 'home-button',
                handler: (btn, evt)  => {
                    if (this.tabPanel) {
                        this.tabPanel.setActiveTab(0);
                    }
                }
            });

            this.leftContainer = Ext.create('Ext.Container', {
                flex: 5,
                padding: 6,
                layout: {
                    type: 'hbox',
                    align: 'middle'
                },
                items: [
                    this.startButton,
                    {
                        xtype: "tbseparator",
                        width: '2px'
                    },
                    this.homeButton
                ]
            });

            this.rightContainer = Ext.create('Ext.Container', {
                flex: 5,
                layout: {
                    type: 'hbox',
                    align: 'middle',
                    pack: 'end'
                }
            });
            var adminImageButton = new admin.ui.AdminImageButton(
                API.util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                '<div class="title">User</div>' +
                '<div class="user-name">{userName}</div>' +
                '<div class="content">' +
                '<div class="column"><img src="{photoUrl}"/>' +
                '<button class="x-btn-red-small">Log Out</button>' +
                '</div>' +
                '<div class="column">' +
                '<span>{qName}</span>' +
                '<a href="#">View Profile</a>' +
                '<a href="#">Edit Profile</a>' +
                '<a href="#">Change User</a>' +
                '</div>' +
                '</div>',
                {
                    userName: "Thomas Lund Sigdestad",
                    photoUrl: API.util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                    qName: 'system/tsi'
                }
            );
            this.rightContainer.add(adminImageButton.ext);

            me.add(this.leftContainer);
            me.add(this.rightContainer);

            if (this.tabPanel) {
                this.tabMenu = new admin.ui.TopBarMenu(this.tabPanel);
                this.titleButton = Ext.create('Ext.button.Button', {
                    cls: 'title-button',
                    menuAlign: 't-b?',
                    menu: this.tabMenu.ext,
                    scale: 'medium',
                    styleHtmlContent: true,
                    text: '<span class="title">Title</span><span class="count">0</span>',

                    setTitle: function (title) {
                        // update title only, without changing count
                        if (this.el) {
                            this.el.down('.title').setHTML(title);
                        }
                    },

                    setCount: function (count) {
                        // update count number only, without changing title
                        if (this.el) {
                            this.el.down('.count').setHTML(count);
                        }
                    }
                });
                me.insert(1, this.titleButton);
            }

            this.syncTabCount();
        }


        toggleHomeScreen():void {
            var isInsideIframe = window.top !== window.self;
            if (isInsideIframe) {
                window.parent['Ext'].getCmp('admin-home-main-container').toggleShowHide();
            } else {
                console.error('Can not toggle home screen. Document must be loaded inside the main window');
            }
        }


        /*  Methods for Admin.view.TabPanel integration */

        insert(index, cfg):any /* Ext.Component */ {
            var added = this.tabMenu.addItems(cfg);
            this.syncTabCount();

            return added.length === 1 ? added[0] : added;
        }

        setActiveTab(tab):void {
            this.tabMenu.markActiveTab(tab);

            var card = tab.card;
            var buttonText = tab.text1;
            var iconClass;

            if ('tab-browse' === card.id) {
                buttonText = '';
            } else if (card.tab.iconClass) {
                iconClass = card.tab.iconClass;
            } else if (card.tab.editing) {
                iconClass = 'icon-icomoon-pencil-32';
            }

            this.titleButton.setIconCls(iconClass);
            this.setTitleButtonText(buttonText);
        }

        remove(tab):any /* Ext.Component */ {

            var removed = this.tabMenu.removeItems(tab);
            this.syncTabCount();

            return removed;
        }

        findNextActivatable():any /* TabPanel */ {
            if (this.tabPanel) {
                // set first browse tab active
                return this.tabPanel.items.get(0);
            }
            return null;
        }

        createMenuItemFromTab(item):Object {
            var cfg = item.initialConfig || item;
            return {
                tabBar: this.ext,
                card: item,
                disabled: cfg.disabled,
                closable: cfg.closable,
                hidden: cfg.hidden && !item.hiddenByLayout, // only hide if it wasn't hidden by the layout itself
                iconSrc: this.getMenuItemIcon(item),
                iconClass: cfg.iconClass,
                editing: cfg.editing || false,
                text1: Ext.String.ellipsis(this.getMenuItemDisplayName(item), 26),
                text2: Ext.String.ellipsis(this.getMenuItemDescription(item), 38)
            };
        }


        /*  Private */

        private syncTabCount():void {
            if (this.tabMenu && this.titleButton) {
                var tabCount = this.tabMenu.getAllItems(false).length;
                // show dropdown button when any tab is open or text when nothing is open
                this.titleButton.setVisible(tabCount > 0);
                this.titleButton.setCount(tabCount);

                API.notify.updateAppTabCount(this.getApplicationId(), tabCount);
            }
        }

        /* For 18/4 demo */
        private getApplicationId():string {
            var urlParamsString = document.URL.split('?'),
                urlParams = Ext.urlDecode(urlParamsString[urlParamsString.length - 1]);

            return urlParams.appId ? urlParams.appId.split('#')[0] : null;
        }

        private getMenuItemIcon(card):string {
            var icon;
            if (card.data && card.data instanceof Ext.data.Model) {
                icon = card.data.get('iconUrl') || card.data.get('image_url');
            }
            return icon;
        }

        private getMenuItemDescription(card):string {
            var desc;
            if (!card.isNew && card.data && card.data instanceof Ext.data.Model) {
                desc = card.data.get('path') || card.data.get('qualifiedName') || card.data.get('displayName');
            }
            if (!desc) {
                // fall back to card title
                desc = card.title;
            }
            return desc;
        }

        private getMenuItemDisplayName(card):string {
            var desc;
            if (!card.isNew && card.data && card.data instanceof Ext.data.Model) {
                desc = card.data.get('displayName') || card.data.get('name');
            }
            if (!desc) {
                // fall back to card title
                desc = card.title;
            }
            return desc;
        }


        /*  Public  */

        setTitleButtonText(text):void {
            this.titleButton.setTitle(text);
            var activeTab = this.titleButton.menu.activeTab;

            if (activeTab) {
                activeTab.text1 = text;
                activeTab.updateTitleContainer();
            }
        }

        getStartButton():any /* Ext.button.Button */ {
            return this.startButton;
        }

        getLeftContainer():any /* Ext.container.Container */ {
            return this.leftContainer;
        }

        getRightContainer():any /* Ext.container.Container */ {
            return this.rightContainer;
        }

    }
}
