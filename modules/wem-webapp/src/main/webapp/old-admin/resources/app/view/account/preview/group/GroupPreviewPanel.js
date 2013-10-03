Ext.define('Admin.view.account.preview.group.GroupPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupPreviewPanel',

    requires: [
        'Admin.view.account.preview.group.GroupPreviewToolbar',
        'Admin.view.WizardPanel',
        'Admin.view.account.MembershipsGraphPanel'
    ],

    autoWidth: true,
    autoScroll: true,

    cls: 'admin-user-preview-panel',
    width: undefined,

    showToolbar: true,

    initComponent: function () {
        var Templates_account_groupPreviewCommonInfo =
    		'<tpl if="type===\'role\'">' +
    		    '<div class="container">' +
    		        '<table>' +
    		            '<thead>' +
    		            '<tr>' +
    		                '<th>Description</th>' +
    		            '</tr>' +
    		            '</thead>' +
    		            '<tbody>' +
    		            '<tr>' +
    		                '<td>{staticDesc}</td>' +
    		            '</tr>' +
    		            '</tbody>' +
    		        '</table>' +
    		    '</div>' +
    		'</tpl>' +
    		'<tpl if="type===\'group\'">' +
    		    '<div class="container">' +
    		        '<table>' +
    		            '<thead>' +
    		            '<tr>' +
    		                '<th colspan="2">Properties</th>' +
    		            '</tr>' +
    		            '</thead>' +
    		            '<tbody>' +
    		            '<tr>' +
    		                '<td class="label">Public:</td>' +
    		                '<td>{[values.public ? "yes" : "no"]}</td>' +
    		            '</tr>' +
    		            '<tr>' +
    		                '<td class="label">Description:</td>' +
    		                '<td>{description}</td>' +
    		            '</tr>' +
    		            '</tbody>' +
    		        '</table>' +
    		    '</div>' +
    		'</tpl>' +
    		'<div class="container">' +
    		    '<table>' +
    		        '<thead>' +
    		        '<tr>' +
    		            '<th colspan="2">Statistics</th>' +
    		        '</tr>' +
    		        '</thead>' +
    		        '<tbody>' +
    		        '<tr>' +
    		            '<td class="label">Member count:</td>' +
    		            '<td>{membersCount}</td>' +
    		        '</tr>' +
    		        '<tr>' +
    		            '<td class="label">Last updated:</td>' +
    		            '<td>{lastModified}</td>' +
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

        var me = this;
        if (this.data && this.data.type === 'role') {
            this.data.staticDesc = this.getRoleDescription(this.data.name);
        }
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
                        columnWidth: 1,
                        cls: 'center',
                        xtype: 'panel',
                        defaults: {
                            border: 0
                        },
                        items: [
                            {
                                height: 70,
                                itemId: 'previewHeader',
                                tpl: Templates_account_userPreviewHeader,
                                data: this.data
                            },
                            {
                                flex: 1,
                                cls: 'center',
                                xtype: 'tabpanel',
                                items: [
                                    {
                                        title: "Members",
                                        itemId: 'membershipsTab',
                                        listeners: {
                                            afterlayout: function () {
                                                if (me.data && !me.graphData) {
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
                        tpl: Templates_account_groupPreviewCommonInfo,
                        data: this.data
                    }
                ]
            }
        ];

        if (this.showToolbar) {
            this.tbar = {
                xtype: 'groupPreviewToolbar',
                isEditable: this.data.editable,
                isRole: this.data.type === 'role'
            };
        }

        this.callParent(arguments);
    },


    setData: function (data) {
        if (data) {
            var me = this;
            this.data = data;

            var previewHeader = this.down('#previewHeader');
            previewHeader.update(data);

            var previewPhoto = this.down('#previewPhoto');
            previewPhoto.update(data);

            var previewInfo = this.down('#previewInfo');
            previewInfo.update(data);

            var membershipsTab = this.down('#membershipsTab');
            delete me.graphData;
            if (membershipsTab.rendered) {
                // Graph panel for some reason does not repaint itself
                membershipsTab.fireEvent('show');
                this.doLayout();
            }
        }
    },

    //TODO: Should be replaced, better move to some kind of service
    getRoleDescription: function (name) {
        if (name === 'Contributors') {
            return 'Sed at commodo arcu. Integer mattis lorem pharetra ligula dignissim. ';
        }
        if (name === 'Developers') {
            return 'Curabitur suscipit condimentum ultrices. Nam dolor sem, suscipit ac faucibus. ';
        }
        if (name === 'Enterprise Administrators') {
            return 'Mauris pellentesque diam in ligula pulvinar luctus. Donec ac elit. ';
        }
        if (name === 'Expert Contributors') {
            return 'Morbi vulputate purus non neque dignissim eu iaculis sapien auctor. ';
        }
        return 'Vivamus tellus turpis, varius vel hendrerit et, commodo vitae ipsum.';
    }

});