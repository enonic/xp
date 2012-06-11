Ext.define( 'Admin.view.account.wizard.group.WizardStepMembersPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepMembersPanel',

    requires: [ 'Admin.plugin.BoxSelect' ],
    border: false,

    initComponent: function()
    {
        var memberKeys = [];
        if (this.modelData && this.modelData.members)
        {
            Ext.Array.each(this.modelData.members, function (member)
            {
                Ext.Array.include(memberKeys, member.key);
            });
        }
        var membersList = {
                        allowBlank: true,
                        minChars: 1,
                        forceSelection : true,
                        triggerOnClick: true,
                        typeAhead: true,
                        xtype:'boxselect',
                        cls: 'cms-groups-boxselect',
                        resizable: false,
                        name: 'members',
                        itemId: 'members',
                        value: memberKeys,
                        store: Ext.create( 'Admin.store.account.AccountStore' ),
                        mode: 'local',
                        displayField: 'displayName',
                        itemClassResolver: function (values)
                        {
                            if (values.type === 'user' && !values.builtIn)
                            {
                                return 'cms-user-item';
                            }
                            if (values.type === 'role' || values.builtIn)
                            {
                                return 'cms-role-item';
                            }
                            else
                            {
                                return 'cms-group-item';
                            }
                        },
                        listConfig: {
                            getInnerTpl: function()
                            {
                                return Templates.common.groupList;
                            }

                        },
                        valueField: 'key',
                        growMin: 75,
                        hideTrigger: true,
                        pinList: false,
                        labelTpl: '<tpl if="type==\'user\'">{displayName} ({qualifiedName})</tpl>' +
                                  '<tpl if="type!=\'user\'">{displayName} ({userStore})</tpl>'
                    };
        var newGroupButton = {
            xtype: 'button',
            action: 'newGroup',
            iconCls: 'icon-group-add-24',
            iconAlign: 'left',
            scale: 'medium',
            width: 'auto',
            text: 'New'
        };
        var formItems = [];
        if (this.modelData && this.modelData.type === 'role')
        {
            var roleDescription = this.getRoleDescription(this.modelData.name);
            var descriptionItem = {
                xtype: 'displayfield',
                fieldLabel: 'Description',
                value: roleDescription
            };
            formItems = [descriptionItem, membersList, newGroupButton];
        }
        else
        {
            formItems = [membersList, newGroupButton];
        }
        this.items = [
            {
                xtype: 'fieldset',
                title: 'Members',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: formItems
            }
        ];

        this.callParent( arguments );
        this.down( '#members' ).getStore().sort('type', 'ASC'); // show group accounts first

        // only select members from same userstore if it is remote
        this.down( '#members' ).getStore().getProxy().extraParams = {
            currentGroupKey: this.getSelectedKey()
        };
    },

    getSelectedUserStore:function ()
    {
        return this.userStore || this.modelData.userStore;
    },

    getSelectedKey: function() {
        return this.modelData ? this.modelData.key : undefined;
    },

    getData: function()
    {
        var selectBox = this.down( 'comboboxselect' );
        var values = selectBox.valueModels;
        var groupsSelected = [];
        Ext.Array.each( values, function(group) {
            var group = {key :group.data.key, name:group.data.name, userStore:group.data.userStore};
            groupsSelected.push(group);
        });
        var userData = { members: groupsSelected };
        return userData;
    },

    //TODO: Should be replaced, better move to some kind of service
    getRoleDescription: function(name)
    {
        if (name === 'Contributors')
        {
            return 'Sed at commodo arcu. Integer mattis lorem pharetra ligula dignissim. ';
        }
        if (name === 'Developers')
        {
            return 'Curabitur suscipit condimentum ultrices. Nam dolor sem, suscipit ac faucibus. ';
        }
        if (name === 'Enterprise Administrators')
        {
            return 'Mauris pellentesque diam in ligula pulvinar luctus. Donec ac elit. ';
        }
        if (name === 'Expert Contributors')
        {
            return 'Morbi vulputate purus non neque dignissim eu iaculis sapien auctor. ';
        }
        return 'Vivamus tellus turpis, varius vel hendrerit et, commodo vitae ipsum.';
    }
} );
