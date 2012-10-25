Ext.define('Admin.view.StartMenu', {
    extend: 'Ext.Component',
    alias: 'widget.startMenu',

    border: false,
    cls: 'menu',

    floating: true,
    hidden: true,
    hideMode: 'display',
    shadow: false,

    tpl: new Ext.XTemplate('<div class="top clearfix">' +
                           '<div class="logout"><img src="../html-templates/images/profile-image.png"/><a href="#">Log out</a></div>' +
                           '<img src="../html-templates/images/enonic-logo.png"/>' +
                           '<div class="search"><input type="text"></div>' +
                           '</div>' +

                           '<div class="center clearfix">' +
                           '<tpl for="items">{[this.renderTile(values)]}</tpl>' +
                           '</div>' +

                           '<div class="controls-container clearfix">' +
                           '<ul class="controls"><li class="checked"></li><li></li><li></li><li></li></ul>' +
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

            renderTile: function (tile) {
                return this.tileTpl.applyTemplate(tile);
            }
        }
    ),

    initComponent: function () {

        this.callParent(arguments);

        this.update(this);
    }
});
