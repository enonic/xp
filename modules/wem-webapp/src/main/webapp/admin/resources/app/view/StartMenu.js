Ext.define('Admin.view.StartMenu', {
    extend: 'Ext.Component',
    alias: 'widget.startMenu',

    border: false,
    cls: 'admin-start-menu',

    autoRender: true,
    renderTo: Ext.getBody(),

    floating: true,
    hidden: true,
    shadow: false,
    width: '100%',

    pageSize: 50, // use paging after 50 tiles only
    currentPage: 1,
    expanded: false,
    tiles: [],

    tpl: new Ext.XTemplate('<div class="top clearfix">' +
                           '<img class="logo" src="../dev/html-templates/images/enonic-logo.png"/>' +
                           '<div class="search"><input type="text"></div>' +
                           '<div class="user">' +
                           '<tpl if="loggedUser"><img src="{[values.loggedUser.img]}"/><a class="logout" href="#">Log out</a>' +
                           '<tpl else><a class="login" href="#">Log in</a></tpl>' +
                           '</div></div>' +

                           '<div class="center clearfix"><div class="scroller"><div class="wrapper">' +
                           '<tpl for="items">{[this.renderTile(xindex, xcount, values, parent)]}</tpl>' +
                           '</div></div></div>' +

                           '<tpl if="pages.length &gt; 1">' +
                           '<div class="pager-container clearfix">' +
                           '<ul class="pager"><tpl for="pages" >' +
                           '<li num="{[xindex]}" class="{[xindex == parent.currentPage ? \"current\" : \"\"]}"></li>' +
                           '</tpl></ul>' +
                           '</div>' +
                           '</tpl>' +

                           '<ul class="bottom clearfix">' +
                           '<li><a href="#">Documentation</a></li>' +
                           '<li><a href="#">Community</a></li>' +
                           '<li><a href="#">About</a></li>' +
                           '</ul>',

        {
            tileTpl: new Ext.XTemplate('<div class="tile {cls}" id="{id}">' +
                                       '<tpl if="contentTpl"><div class="content">{[values.contentTpl.applyTemplate(values)]}</div></tpl>' +
                                       '<span class="title">{title}</span>' +
                                       '</div>'),

            compiled: true,
            disableFormats: true,

            renderTile: function (num, total, tile, menu) {

                var start = (menu.currentPage - 1) * menu.pageSize;
                var end = Ext.Array.min([total, start + menu.pageSize]);

                if (!tile.id) {
                    tile.id = Ext.id();
                }

                menu.tiles.push(tile);

                if (num > start && num <= end) {
                    return this.tileTpl.applyTemplate(tile);
                } else {
                    return "";
                }
            }
        }),

    initComponent: function () {
        var me = this;
        this.callParent(arguments);
        this.addEvents('login', 'logout', 'pagechange', 'tileclick');
        this.on('afterrender', me.bindGlobalListeners);
        this.refresh();
    },

    refresh: function () {
        var i;
        // create pages array
        var count = Math.ceil(this.items.length / this.pageSize);
        this.pages = [];
        for (i = 0; i < count; i++) {
            this.pages.push(i);
        }
        // clear current tiles array
        this.tiles = [];
        this.update(this);
        if (this.rendered) {
            this.bindListeners(this);
        } else {
            this.on('afterrender', this.bindListeners);
        }

    },

    bindGlobalListeners: function (me) {
        me.el.on('click', function (event, target, opts) {
            var el = Ext.fly(target);
            var tileEl = el.hasCls('tile') ? el : el.up('div.tile');
            if (tileEl) {
                var id = tileEl.getAttribute('id');
                var tile, t, i;
                for (i = 0; i < me.tiles.length; i++) {
                    t = me.tiles[i];
                    if (t.id === id) {
                        tile = t;
                        break;
                    }
                }
                if (tile) {
                    me.fireEvent('tileclick', tile);
                }
            }
        });
        Ext.getBody().on('click', function (event, target, opts) {
            if (me.isExpanded() && Ext.fly(target).up('div.' + me.cls) === null) {
                me.slideOut();
            }
        });
    },

    bindListeners: function (me) {
        var login = me.el.down('a.login');
        if (login) {
            login.on('click', function (event, target, opts) {
                me.fireEvent("login", me);
                me.refresh();
                // stop event because the target is detached after refresh
                event.stopEvent();
            });
        }
        var logout = me.el.down('a.logout');
        if (logout) {
            logout.on('click', function (event, target, opts) {
                me.fireEvent('logout', me);
                me.refresh();
                // stop event because the target is detached after refresh
                event.stopEvent();
            });
        }
        var pageLinks = me.el.query('.pager li');
        Ext.Array.each(pageLinks, function (li) {
            li = Ext.fly(li);
            if (!li.hasCls('current')) {
                li.on('click', function (event, target, opts) {
                    me.currentPage = parseInt(target.getAttribute("num"));
                    me.refresh();
                    me.fireEvent('pagechange', me, me.currentPage);
                    // stop event because the target is detached after refresh
                    event.stopEvent();
                });
            }
        });
        if (me.isExpanded()) {
            me.updateScrollWidth();
        }
    },

    updateScrollWidth: function () {
        // set the scroller width equal to this of content
        var scroller = this.el.down('.scroller');
        var wrapper = scroller.down('.wrapper');
        scroller.setWidth(wrapper.getWidth());
    },

    isExpanded: function () {
        return this.expanded;
    },

    slideToggle: function () {
        if (this.isExpanded()) {
            this.slideOut();
        } else {
            this.slideIn();
        }
    },

    slideIn: function (callback) {
        var me = this;
        if (this.isHidden()) {
            this.showAt(0, 0);
            me.updateScrollWidth();
        }
        this.el.slideIn('t', {
            callback: function () {
                me.expanded = true;
                if (Ext.isFunction(callback)) {
                    callback.call(me);
                }
            }
        });
    },

    slideOut: function (callback) {
        var me = this;
        this.el.slideOut('t', {
            callback: function () {
                me.expanded = false;
                if (Ext.isFunction(callback)) {
                    callback.call(me);
                }
            }
        });
    }

});
