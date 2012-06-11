Ext.define( 'Admin.view.userstore.wizard.UserstoreWizardAdminPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.userstoreWizardAdminPanel',

    requires: [ 'Admin.plugin.BoxSelect' ],


    initComponent: function()
    {
        var me = this;
        var memberKeys = [];
        if (this.modelData && this.modelData.administrators)
        {
            Ext.Array.each(this.modelData.administrators, function (member)
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
            name: 'administrators',
            itemId: 'administrators',
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
        this.items = [
            {
                xtype: 'fieldset',
                title: 'Administrators',
                padding: '10px 15px',
                defaults: {
                    width: 600
                },
                items: [membersList]
            }
        ];
        me.callParent( arguments );
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
        var userData = { administrators: groupsSelected };
        return userData;
    }
} );
