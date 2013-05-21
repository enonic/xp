Ext.define('Admin.view.account.preview.user.UserPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreviewPanel',
    requires: [
        'Admin.view.account.preview.user.UserPreviewToolbar',
        'Admin.view.WizardPanel',
        'Admin.view.account.MembershipsGraphPanel'
    ],

    dialogTitle: 'User Preview',

    autoWidth: true,
    autoScroll: true,

    cls: 'admin-user-preview-panel',
    width: undefined,

    showToolbar: true,

    initComponent: function () {
        var Templates_account_userPreviewCommonInfo =
        		'<div class="container">' +
        		    '<table>' +
        		        '<thead>' +
        		        '<tr>' +
        		            '<th>Login Info</th>' +
        		        '</tr>' +
        		        '</thead>' +
        		        '<tbody>' +
        		        '<tr>' +
        		            '<td class="label">User Name:</td>' +
        		            '<td>{name}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">E-mail:</td>' +
        		            '<td>{email}</td>' +
        		        '</tr>' +
        		        '</tbody>' +
        		    '</table>' +
        		'</div>' +
        		'<div class="container admin-groups-boxselect">' +
        		    '<table>' +
        		        '<thead>' +
        		        '<tr>' +
        		            '<th>Roles</th>' +
        		        '</tr>' +
        		        '</thead>' +
        		        '<tbody>' +
        		        '<tpl for="groups">' +
        		            '<tpl if="type == \'role\'">' +
        		                '<tr>' +
        		                    '<td>' +
        		                        '<li class="x-boxselect-item admin-{type}-item">' +
        		                            '<div class="x-boxselect-item-text">{qualifiedName}</div>' +
        		                        '</li>' +
        		                    '</td>' +
        		                '</tr>' +
        		            '</tpl>' +
        		        '</tpl>' +
        		        '</tbody>' +
        		    '</table>' +
        		'</div>' +
        		'<div class="container">' +
        		    '<table>' +
        		        '<thead>' +
        		        '<tr>' +
        		            '<th colspan="2">Settings</th>' +
        		        '</tr>' +
        		        '</thead>' +
        		        '<tbody>' +
        		        '<tr>' +
        		            '<td class="label">Locale:</td>' +
        		            '<td>{locale}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">Country:</td>' +
        		            '<td>{country}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">TimeZone:</td>' +
        		            '<td>{timezone}</td>' +
        		        '</tr>' +
        		        '</tbody>' +
        		    '</table>' +
        		'</div>' +
        		'<div class="container">' +
        		    '<table>' +
        		        '<thead>' +
        		        '<tr>' +
        		            '<th colspan="2">Statistics</th>' +
        		        '</tr>' +
        		        '</thead>' +
        		        '<tbody>' +
        		        '<tr>' +
        		            '<td class="label">Last login:</td>' +
        		            '<td>{lastLogged}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">Created:</td>' +
        		            '<td>{created}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">Owner of:</td>' +
        		            '<td>394</td>' +
        		        '</tr>' +
        		        '</tbody>' +
        		    '</table>' +
        		'</div>';

        var Templates_account_userPreviewHeader =
        		'<div class="container">' +
        		    '<h1>{displayName}</h1>' +
        		    '<div>' +
        		        '<span>{userStore}\\\\{name}</span><!--<span class="email">&nbsp;{email}</span>-->' +
        		    '</div>' +
        		'</div>';

        var Templates_account_userPreviewMemberships =
    		'<fieldset class="x-fieldset x-fieldset-default admin-memberships-container">' +
    		    '<legend class="x-fieldset-header x-fieldset-header-default">' +
    		        '<div class="x-component x-fieldset-header-text x-component-default">Graph</div>' +
    		    '</legend>' +
    		'</fieldset>';

        var Templates_account_userPreviewPhoto =
    		'<div class="admin-user-photo west admin-left">' +
    		    '<div class="photo-placeholder">' +
    		        '<img src="{[values.image_url]}?size=100" alt="{name}"/>' +
    		    '</div>' +
    		'</div>';

        var Templates_account_userPreviewPlaces =
    		'<tpl if="userInfo == null || userInfo.addresses == null || userInfo.addresses.length == 0">' +
    		    '<h2 class="message">No data</h2>' +
    		'</tpl>' +
    		'<tpl if="profile != null && profile.addresses != null && profile.addresses.length &gt; 0">' +
    		    '<fieldset class="x-fieldset x-fieldset-default admin-addresses-container">' +
    		        '<legend class="x-fieldset-header x-fieldset-header-default">' +
    		            '<div class="x-component x-fieldset-header-text x-component-default">Addresses</div>' +
    		        '</legend>' +
    		        '<tpl for="profile.addresses">' +
    		            '<div class="address">' +
    		                '<tpl if="label != null">' +
    		                    '<h3 class="x-fieldset-header-text">{label}</h3>' +
    		                '</tpl>' +
    		                '<div class="body">' +
    		                    '<table>' +
    		                        '<tbody>' +
    		                        '<tr>' +
    		                            '<td class="label">Street:</td>' +
    		                            '<td>{street}</td>' +
    		                        '</tr>' +
    		                        '<tr>' +
    		                            '<td class="label">Postal Code:</td>' +
    		                            '<td>{postalCode}</td>' +
    		                        '</tr>' +
    		                        '<tr>' +
    		                            '<td class="label">Postal Address:</td>' +
    		                            '<td>{postalAddress}</td>' +
    		                        '</tr>' +
    		                        '<tr>' +
    		                            '<td class="label">Country:</td>' +
    		                            '<td>{country}</td>' +
    		                        '</tr>' +
    		                        '<tr>' +
    		                            '<td class="label">Region:</td>' +
    		                            '<td>{region}</td>' +
    		                        '</tr>' +
    		                        '</tbody>' +
    		                    '</table>' +
    		                '</div>' +
    		            '</div>' +
    		        '</tpl>' +
    		    '</fieldset>' +
    		'</tpl>';

        var Templates_account_userPreviewProfile =
            '<div>' +
                '<tpl for=".">' +
                    '<fieldset class="x-fieldset x-fieldset-default">' +
                        '<legend class="x-fieldset-header x-fieldset-header-default">' +
                            '<div class="x-component x-fieldset-header-text x-component-default">{title}</div>' +
                        '</legend>' +
                        '<table>' +
                            '<tbody>' +
                            '<tpl for="fields">' +
                                '<tr>' +
                                    '<td class="label">{title}</td>' +
                                    '<td>{value}</td>' +
                                '</tr>' +
                            '</tpl>' +
                            '</tbody>' +
                        '</table>' +
                    '</fieldset>' +
                '</tpl>' +
            '</div>';

        this.fieldSets = [
            {
                title: 'Name',
                fields: [ 'prefix', 'firstName', 'middleName',
                    'lastName', 'suffix', 'initials', 'nickName']
            },
            {
                title: 'Personal Information',
                fields: ['personalId', 'memberId', 'organization', 'birthday',
                    'gender', 'title', 'description', 'htmlEmail', 'homepage']
            },
            {
                title: 'Settings',
                fields: ['timezone', 'locale', 'country', 'globalPosition']
            },
            {
                title: 'Communication',
                fields: ['phone', 'mobile', 'fax']
            }
        ];
        var profileData = this.generateProfileData(this.data);

        if (this.showToolbar) {
            this.tbar = {
                xtype: 'userPreviewToolbar',
                isEditable: this.data.editable
            };
        }

        var me = this;

        this.items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'column',
                    columns: 3
                },
                autoHeight: true,
                defaults: {
                    border: 0
                },
                items: [
                    {
                        width: 100,
                        itemId: 'previewPhoto',
                        tpl: Templates_account_userPreviewPhoto,
                        data: this.data,
                        margin: 5
                    },
                    {
                        columnWidth: 0.98,
                        cls: 'center',
                        xtype: 'panel',
                        defaults: {
                            border: 0
                        },
                        items: [
                            {
                                autoHeight: true,
                                itemId: 'previewHeader',
                                tpl: Templates_account_userPreviewHeader
                            },
                            {
                                flex: 1,
                                itemId: 'previewTabs',

                                xtype: 'tabpanel',
                                defaults: {
                                    border: false
                                },
                                items: [
                                    {
                                        title: "Profile",
                                        itemId: 'profileTab',
                                        tpl: Templates_account_userPreviewProfile
                                    },
                                    {
                                        title: "Places",
                                        itemId: 'placesTab',
                                        tpl: Templates_account_userPreviewPlaces
                                    },
                                    {
                                        title: "Memberships",
                                        itemId: 'membershipsTab',
                                        listeners: {
                                            afterlayout: function () {
                                                if (!me.graphData) {
                                                    var mask = new Ext.LoadMask(this, {msg: "Please wait..."});
                                                    mask.show();
                                                    Admin.lib.RemoteService.account_getGraph({key: me.data.key}, function (r) {
                                                        if (r && r.success) {
                                                            me.graphData = r.graph;
                                                            me.down('membershipsGraphPanel').setGraphData(me.graphData);
                                                        }
                                                        mask.hide();
                                                    });
                                                }
                                            }
                                        },
                                        items: [
                                            {
                                                tpl: Templates_account_userPreviewMemberships
                                            },
                                            {
                                                xtype: 'membershipsGraphPanel',
                                                extraCls: 'admin-memberships-graph'
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        width: 300,
                        margin: 5,
                        itemId: 'previewInfo',
                        cls: 'east',
                        tpl: Templates_account_userPreviewCommonInfo
                    }
                ]
            }
        ];
        this.callParent(arguments);


    },

    listeners: {
        afterrender: function () {
            this.setData(this.data);
        }
    },

    generateProfileData: function (userData) {
        var fieldSetEmpty = true;
        var profileData = [];
        if (userData) {
            Ext.Array.each(this.fieldSets, function (fieldSet) {
                var fieldSetData = { title: fieldSet.title};
                fieldSetData.fields = [];
                Ext.Array.each(fieldSet.fields, function (field) {
                    var value = userData[field] || (userData.profile ? userData.profile[field] : undefined);
                    var title = Admin.view.account.EditUserFormPanel.fieldLabels[field] || field;
                    if (value) {
                        Ext.Array.include(fieldSetData.fields, {title: title, value: value});
                        fieldSetEmpty = false;
                    }
                });
                if (!fieldSetEmpty) {
                    Ext.Array.include(profileData, fieldSetData);
                    fieldSetEmpty = true;
                }
            });
        }
        return profileData;
    },

    isFieldsEnabled: function (fieldNames, userstoreName) {
        var userstores = Ext.data.StoreManager.lookup('Admin.store.account.UserstoreConfigStore');
        var userstore = userstores.findRecord('name', userstoreName);
        if (userstore && userstore.raw.userFields) {
            var fieldTypes = [].concat(fieldNames);
            var i;
            var j;
            for (i = 0; i < userstore.raw.userFields.length; i++) {
                for (j = 0; j < fieldTypes.length; j++) {
                    if (userstore.raw.userFields[i].type === fieldTypes[j]) {
                        return true;
                    }
                }
            }
        }
        return false;
    },

    setData: function (data) {
        if (data) {
            this.data = data;

            var tabs = this.down('#previewTabs');

            var previewHeader = this.down('#previewHeader');
            previewHeader.update(data);

            var previewPhoto = this.down('#previewPhoto');
            previewPhoto.update(data);

            var previewInfo = this.down('#previewInfo');
            previewInfo.update(data);

            var profileTab = this.down('#profileTab');
            var profileFields = [];
            var i;
            for (i = 0; i < this.fieldSets.length; i++) {
                profileFields = profileFields.concat(this.fieldSets[i].fields);
            }
            if (this.isFieldsEnabled(profileFields, data.userStore)) {
                profileTab.update(this.generateProfileData(data));
                this.setTabVisible(tabs, profileTab, true);
            } else {
                this.setTabVisible(tabs, profileTab, false);
            }

            var placesTab = this.down('#placesTab');
            if (this.isFieldsEnabled('address', data.userStore)) {
                placesTab.update(data);
                this.setTabVisible(tabs, placesTab, true);
            } else {
                this.setTabVisible(tabs, placesTab, false);
            }

            var membershipsTab = this.down('#membershipsTab');

            if (membershipsTab.rendered) {
                delete this.graphData;
                membershipsTab.fireEvent("show");
            }
        }
    },

    setTabVisible: function (tabPanel, tabItem, visible) {
        tabItem.tab.setVisible(visible);
        // activate first tab if the item being hidden is active
        var tabLayout = tabPanel.getLayout();
        if (!visible && tabLayout.getActiveItem() === tabItem) {
            tabPanel.items.each(function (item, index) {
                if (item.tab.isVisible()) {
                    tabLayout.setActiveItem(index);
                    return false;
                }
                return true;
            });

        }
    }



});