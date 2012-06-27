Ext.define('Admin.view.account.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.accountGrid',

    requires: [
        'Admin.plugin.PersistentGridSelectionPlugin',
        'Admin.plugin.SlidingPagerPlugin'
    ],
    plugins: ['persistentGridSelection'],
    layout: 'fit',
    multiSelect: true,
    columnLines: true,
    frame: false,
    store: 'Admin.store.account.AccountStore',

    initComponent: function () {
        this.columns = [
            {
                text: 'Display Name',
                dataIndex: 'displayName',
                sortable: true,
                renderer: this.nameRenderer,
                flex: 1
            },
            {
                text: 'Username',
                dataIndex: 'name',
                hidden: true,
                sortable: true
            },
            {
                text: 'Userstore',
                dataIndex: 'userStore',
                hidden: true,
                sortable: true
            },
            {
                text: 'E-Mail',
                dataIndex: 'email',
                hidden: true,
                sortable: true
            },
            {
                text: 'Country',
                dataIndex: 'country',
                hidden: true,
                sortable: true
            },
            {
                text: 'Locale',
                dataIndex: 'locale',
                hidden: true,
                sortable: true
            },
            {
                text: 'Timezone',
                dataIndex: 'timezone',
                hidden: true,
                sortable: true
            },
            {
                text: 'Last Modified',
                dataIndex: 'lastModified',
                renderer: this.prettyDateRenderer,
                sortable: true
            }
        ];

        this.tbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            plugins: ['slidingPagerPlugin']
        };

        this.viewConfig = {
            trackOver: true,
            stripeRows: true,
            loadMask: true
        };

        this.selModel = Ext.create('Ext.selection.CheckboxModel', {
            //checkOnly: true
        });

        this.callParent(arguments);
    },

    nameRenderer: function (value, p, record) {
        var account = record.data;
        var photoUrl = this.resolvePhotoUrl(account);

        return Ext.String.format(Templates.account.gridPanelNameRenderer, photoUrl, value, account.name, account.userStore);
    },

    resolvePhotoUrl: function (account) {
        var url;
        var builtIn = account.builtIn;
        var isEnterpriseAdministrator = builtIn && account.name === 'admin';
        var isAnonymous = builtIn && account.name === 'anonymous';
        // TODO: Remove && !isAnonymous when EA and Anonymous has been re-classified as "user" (B-02818)
        var isRole = builtIn && account.type === 'role' && !isAnonymous;
        var isUser = !builtIn && account.type === 'user';

        if (isUser) {
            url = Ext.String.format('data/user/photo?key={0}&thumb=true&def=admin/resources%2Fimages%2Ficons%2F256x256%2Fdummy-user.png',
                account.key);
        } else {
            if (isEnterpriseAdministrator) {
                url = 'resources/images/icons/32x32/superhero.png';
            } else if (isAnonymous) {
                url = 'resources/images/icons/32x32/ghost.png';
            } else if (isRole) {
                url = 'resources/images/icons/32x32/masks.png';
            } else {
                url = 'resources/images/icons/32x32/group.png';
            }
        }

        return url;
    },

    prettyDateRenderer: function (value, p, record) {
        try {
            if (parent && Ext.isFunction(parent.humane_date)) {
                return parent.humane_date(value);
            } else {
                return value;
            }
        }
        catch (e) {
            return value;
        }
    }
});
