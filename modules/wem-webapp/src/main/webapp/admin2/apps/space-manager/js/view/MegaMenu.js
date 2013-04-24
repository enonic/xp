Ext.define('Admin.view.MegaMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'megaMenu',

    requires: [
        'Admin.view.MegaKeyNav',
        'Ext.util.Cookies'
    ],

    bodyCls: 'admin-mega-menu',
    plain: true,
    bodyPadding: 0,
    showSeparator: false,
    styleHtmlContent: true,
    minWidth: 60,

    layout: {
        type: 'hbox',
        autoSize: true,
        align: 'stretchmax'
    },

    recentCount: 6,
    cookieKey: 'admin.main.megamenu',
    cookieSeparator: '|',


    initComponent: function () {

        this.items = [];

        if (this.recentCount > 0) {
            this.items.push(this.createRecentContainer());
            this.layout.columns = 2;
        }
        this.items.push(this.createItemContainer());

        if (Ext.isString(this.url)) {

            this.loader = {
                url: this.url,
                autoLoad: true,
                renderer: function (loader, response, active) {

                    var menu = loader.getTarget();
                    menu.updateItemsFromResponse(Ext.decode(response.responseText));
                    return true;
                }
            };

        } else if (Ext.isFunction(this.url)) {
            this.url.call(this);
        }

        this.callParent(arguments);

        if (this.recentCount > 0) {
            this.on('click', this.onMenuItemClick);
        }
        this.on('beforeshow', this.updateRecentItems);

    },

    afterRender: function (ct) {
        var me = this;
        this.callParent(arguments);
        if (this.keyNav) {
            this.keyNav.destroy();
        }
        this.keyNav = new Ext.create('Admin.view.MegaKeyNav', me);
        this.createItemMap();
    },


    onMenuItemClick: function (menu, item, evt, opts) {
        var recentCookie = Ext.util.Cookies.get(this.cookieKey);
        var recentArray = recentCookie ? recentCookie.split(',') : [];

        var recentItem = [item.text, item.icon, item.action, item.cms ? item.cms.appUrl : ''].join(this.cookieSeparator);
        if (recentArray.length === 0 || recentArray[0] !== recentItem) {
            recentArray.unshift(recentItem);
        }

        if (recentArray.length > this.recentCount) {
            // constrain recent items quantity to 10
            recentArray = recentArray.slice(0, this.recentCount);
        }

        Ext.util.Cookies.clear(this.cookieKey);
        Ext.util.Cookies.set(this.cookieKey, recentArray.join(','));
    },


    createRecentContainer: function () {

        var recentSection = this.createMenuSection('Recent', [this.createPlaceholder("Recent items will<br/> be shown here")]);

        return Ext.apply(recentSection, {
            itemId: 'recentSection',
            layout: {
                type: 'table',
                columns: 1,
                tdAttrs: {
                    style: {
                        'vertical-align': 'top'
                    }
                }
            },
            cls: recentSection.cls + ' recent'
        });
    },

    updateRecentItems: function () {
        var me = this;

        // update in case recent is enabled only
        if (me.recentCount > 0) {
            var recentItems = [];

            var recentCookie = Ext.util.Cookies.get(this.cookieKey);
            var recentArray = recentCookie ? recentCookie.split(',') : [];

            Ext.Array.each(recentArray, function (item, index, all) {
                var itemArray = item.split(me.cookieSeparator);
                recentItems.push(me.createMenuItem({
                    text: itemArray[0],
                    icon: itemArray[1],
                    action: itemArray[2],
                    cms: {
                        appUrl: itemArray[3]
                    }
                }));
            });

            if (recentItems.length === 0) {
                recentItems.push(this.createPlaceholder("Recent items will<br/> be shown here"));
            }

            var recentSection = this.down('#recentSection');
            if (recentSection) {
                recentSection.removeAll(true);
                recentSection.add(recentItems);
            }
        }
    },

    createItemContainer: function () {
        var itemSection = this.createMenuSection(null, [this.createPlaceholder("Loading...")]);

        return Ext.apply(itemSection, {
            itemId: 'itemSection',
            cls: '',
            layout: {
                type: 'table',
                columns: 1,
                tdAttrs: {
                    style: {
                        'vertical-align': 'top'
                    }
                }
            }
        });
    },

    updateItemsFromResponse: function (data) {
        var menuItems = [];
        var columns = 0;

        if (data && data.menu) {
            var i;
            var j;
            for (i = 0; i < data.menu.items.length; i++) {

                var section = data.menu.items[i];
                var sectionItems = [];

                if (section.menu && section.menu.items.length > 0) {
                    for (j = 0; j < section.menu.items.length; j++) {
                        sectionItems.push(this.createMenuItem(section.menu.items[j]));
                    }
                    menuItems.push(this.createMenuSection(section.text, sectionItems));
                    if (!Ext.isEmpty(section.text)) {
                        columns++;
                    }
                }
            }
        }

        if (menuItems.length === 0) {
            menuItems.push(this.createPlaceholder("No items"));
        }

        var itemSection = this.down('#itemSection');
        if (menuItems.length > 0 && itemSection) {
            itemSection.layout.columns = columns;
            itemSection.removeAll(true);
            itemSection.add(menuItems);
        }
    },

    createPlaceholder: function (text) {
        return {
            xtype: 'component',
            styleHtmlContent: true,
            html: '<p class="nodata">' + text + '</p>'
        };
    },


    createMenuItem: function (item) {
        return Ext.apply({
            xtype: 'menuitem',
            cls: 'admin-mega-menu-item medium'
        }, item);
    },

    createMenuSection: function (header, items) {
        var hasHeader = !Ext.isEmpty(header);
        return {
            xtype: 'panel',
            title: hasHeader ? '<h2>' + header + '</h2>' : undefined,
            cls: 'admin-mega-menu-section',
            border: false,
            plain: true,
            colspan: hasHeader ? 1 : 4,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: items
        };
    },


    createItemMap: function () {
        var result = [];
        var itemSection = this.down('#itemSection');

        var currentCol = 0;
        if (itemSection && itemSection.items) {
            itemSection.items.each(function (container, colIndex, colAll) {

                if (container.colspan === 1) {
                    result[currentCol] = [];
                    container.items.each(function (item, rowIndex, rowAll) {
                        result[currentCol].push(item);
                    });
                    currentCol++;
                }

            });
        }
        this.itemMap = result;
    },

    getItemPosition: function (item) {
        var i;
        var j;
        for (i = 0; i < this.itemMap.length; i++) {
            for (j = 0; j < this.itemMap[i].length; j++) {
                if (this.itemMap[i][j] === item) {
                    return [j, i];
                }
            }
        }
    },

    getItemAbove: function (item) {
        return this.getItemNear(item, -1, 0);
    },

    getItemBelow: function (item) {
        return this.getItemNear(item, 1, 0);
    },

    getItemLeft: function (item) {
        return this.getItemNear(item, 0, -1);
    },

    getItemRight: function (item) {
        return this.getItemNear(item, 0, 1);
    },

    getItemNear: function (item, hor, ver) {
        var xy = this.getItemPosition(item);
        var x, y;
        if (xy) {
            x = xy[0] + hor;
            y = xy[1] + ver;
        } else {
            // set the first item selected by default
            x = 0;
            y = 0;
        }

        // handle y edges
        var yLength = this.itemMap.length;
        if (y < 0) {
            y += yLength;
        } else if (y >= yLength) {
            y -= yLength;
        }
        // handle x edges, should be done after y because we need to know the row first
        var xLength = this.itemMap[y].length;
        if (x < 0) {
            x += xLength;
        } else if (x >= xLength) {
            if (!hor || hor === 0) {
                // came from the row with more items, so set to the last
                x = xLength - 1;
            } else {
                // went out of right horizontal limit, so start over
                x -= xLength;
            }
        }

        return this.itemMap[y][x];
    },

    getItemFromEvent: function (e) {
        var item = this;
        do {
            item = item.getChildByElement(e.getTarget());
        }
        while (item && Ext.isDefined(item.getChildByElement) && item.getXType() !== 'menuitem');
        return item;
    }

});
