Ext.define('Admin.view.userstore.wizard.UserstoreWizardAdminPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userstoreWizardAdminPanel',

    requires: [ 'Admin.plugin.BoxSelect' ],


    initComponent: function () {
        var me = this;
        var memberKeys = (this.modelData && this.modelData.administrators) ? this.modelData.administrators : [];
        var membersList = {
            allowBlank: true,
            minChars: 1,
            forceSelection: true,
            triggerOnClick: true,
            typeAhead: true,
            xtype: 'boxselect',
            cls: 'admin-groups-boxselect',
            resizable: false,
            name: 'administrators',
            itemId: 'administrators',
            value: memberKeys,
            store: Ext.create('Admin.store.account.AccountStore'),
            mode: 'local',
            displayField: 'displayName',
            itemClassResolver: function (values) {
                if (values.type === 'user' && !values.builtIn) {
                    return 'admin-user-item';
                }
                if (values.type === 'role' || values.builtIn) {
                    return 'admin-role-item';
                } else {
                    return 'admin-group-item';
                }
            },
            listConfig: {
                getInnerTpl: function () {
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
        me.callParent(arguments);
    },

    getData: function () {
        var selectBox = this.down('comboboxselect');
        var values = selectBox.valueModels;
        var groupsSelected = [];
        Ext.Array.each(values, function (group) {
            groupsSelected.push(group.data.key);
        });
        var userData = { administrators: groupsSelected };
        return userData;
    }
});
