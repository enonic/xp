Ext.define('Admin.lib.formitem.FromCreator', {

    addFormItems: function (contentTypeItems, parentContainer) {
        var me = this;
        var formItem;

        Ext.each(contentTypeItems, function (item) {
            if (item.FormItemSet) {
                formItem = me.createFormItemSet(item.FormItemSet);
            } else { // Input
                var classAlias = 'widget.' + item.Input.type.name;
                if (!me.formItemIsSupported(classAlias)) {
                    console.error('Unsupported input type', item.Input);
                    return;
                }

                formItem = me.createItem(item.Input);
            }

            if (parentContainer.getXType() === 'FormItemSet' || parentContainer.getXType() === 'fieldcontainer' || parentContainer.getXType() === 'container') {
                parentContainer.add(formItem);
            } else {
                parentContainer.items.push(formItem);
            }
        });
    },


    createFormItemSet: function (formItemSetConfig) {
        return Ext.create({
            xclass: 'widget.FormItemSet',
            name: formItemSetConfig.name,
            formItemSetConfig: formItemSetConfig
        });
    },


    createItem: function (inputConfig) {
        var me = this;
        var classAlias = 'widget.' + inputConfig.type.name;

        return Ext.create({
            xclass: classAlias,
            fieldLabel: inputConfig.label,
            name: inputConfig.name,
            inputConfig: inputConfig
        });

    },


    formItemIsSupported: function (classAlias) {
        return Ext.ClassManager.getByAlias(classAlias);
    }

});