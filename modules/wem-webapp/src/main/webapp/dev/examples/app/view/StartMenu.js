Ext.define('Admin.view.StartMenu', {
    extend: 'Ext.Component',
    alias: 'widget.startMenu',

    border: false,
    cls: 'menu',

    autoRender: true,
    renderTo: Ext.getBody(),

    floating: true,
    hidden: true,
    shadow: false,
    width: '100%',

    currentPage: 1,
    pageSize: 7,

    tpl: new Ext.XTemplate('<div class="top clearfix">' +
                           '<img class="logo" src="../html-templates/images/enonic-logo.png"/>' +
                           '<div class="search"><input type="text"></div>' +
                           '<div class="user">' +
                           '<tpl if="loggedUser"><img src="{[values.loggedUser.img]}"/><a class="logout" href="#">Log out</a>' +
                           '<tpl else><a class="login" href="#">Log in</a></tpl>' +
                           '</div></div>' +

                           '<div class="center clearfix">' +
                           '<tpl for="items">{[this.renderTile(xindex, values, parent)]}</tpl>' +
                           '</div>' +

                           '<div class="pager-container clearfix">' +
                           '<ul class="pager"><tpl for="pages" >' +
                           '<li num="{[xindex]}" class="{[xindex == parent.currentPage ? \"current\" : \"\"]}"></li>' +
                           '</tpl></ul>' +
                           '</div>' +

                           '<ul class="bottom clearfix">' +
                           '<li><a href="#">Documentation</a></li>' +
                           '<li><a href="#">Community</a></li>' +
                           '<li><a href="#">About</a></li>' +
                           '</ul>',

        {
            tileTpl: new Ext.XTemplate('<div class="tile {cls}">' +
                                       '<tpl if="contentTpl"><div class="content">{[values.contentTpl.applyTemplate(values)]}</div></tpl>' +
                                       '<span class="title">{title}</span>' +
                                       '</div>'),

            compiled: true,
            disableFormats: true,

            renderTile: function (num, tile, menu) {
                var page = menu.currentPage;
                var size = menu.pageSize;
                var start = (page - 1) * size;
                var end = start + size;
                if (num > start && num <= end) {
                    return this.tileTpl.applyTemplate(tile);
                } else {
                    return "";
                }
            }
        }
    ),

    initComponent: function () {
        this.callParent(arguments);
        this.addEvents('login', 'logout', 'pagechange');
        this.refresh();
    },

    refresh: function () {
        var i;
        var count = Math.ceil(this.items.length / this.pageSize);
        this.pages = [];
        for (i = 0; i < count; i++) {
            this.pages.push(i);
        }
        this.update(this);
        if (this.rendered) {
            this.bindListeners(this);
        } else {
            this.on('afterrender', this.bindListeners);
        }

    },

    bindListeners: function (me) {
        var login = me.el.down('a.login');
        if (login) {
            login.on('click', function () {
                me.fireEvent("login", me);
                me.refresh();
            });
        }
        var logout = me.el.down('a.logout');
        if (logout) {
            logout.on('click', function () {
                me.fireEvent('logout', me);
                me.refresh();
            });
        }
        var pageLinks = me.el.query('.pager li');
        Ext.Array.each(pageLinks, function (li) {
            li = Ext.fly(li);
            if (!li.hasCls('current')) {
                li.on('click', function (event, target) {
                    me.currentPage = parseInt(target.getAttribute("num"));
                    me.refresh();
                    me.fireEvent('pagechange', me, me.currentPage);
                });
            }
        });
    },

    slideToggle: function () {
        if (this.el.isVisible()) {
            this.slideOut();
        } else {
            this.slideIn();
        }
    },

    slideIn: function () {
        if (this.isHidden()) {
            this.showAt(0, 40);
        }
        this.el.slideIn();
    },

    slideOut: function () {
        this.el.slideOut();
    }

});
