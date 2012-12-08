Ext.define('Admin.lib.formitem.FormHelper', {

    addFormItemsForEditForm: function (content, contentTypeDef, parentContainer) {
        var me = this;

        Ext.Array.each(content.data, function(contentItem) {

            var configForContentItem = me.getCtyConfigForContentItem(contentItem, contentTypeDef.form);
            var formItem;

            if (configForContentItem.FormItemSet) {
                formItem = me.createFormItemSet(configForContentItem.FormItemSet);
            } else { // Input

                console.log(configForContentItem.Input.type.name);
                var classAlias = 'widget.' + configForContentItem.Input.type.name;

                if (!me.formItemIsSupported(classAlias)) {
                    console.error('Unsupported input type', item.Input);
                    return;
                }

                formItem = me.createFormItemComponent(configForContentItem.Input, contentItem.value);
            }

            if (parentContainer.getXType() === 'FormItemSet' || parentContainer.getXType() === 'fieldcontainer' || parentContainer.getXType() === 'container') {
                parentContainer.add(formItem);
            } else {
                parentContainer.items.push(formItem);
            }

        });
    },

    addFormItemsForNewForm: function (contentTypeItems, parentContainer) {
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

                formItem = me.createFormItemComponent(item.Input);
            }

            if (parentContainer.getXType() === 'FormItemSet' || parentContainer.getXType() === 'fieldcontainer' || parentContainer.getXType() === 'container') {
                parentContainer.add(formItem);
            } else {
                parentContainer.items.push(formItem);
            }
        });
    },


    createFormItemSet: function (formItemSetConfig, dataValue) {
        return Ext.create({
            xclass: 'widget.FormItemSet',
            name: formItemSetConfig.name,
            formItemSetConfig: formItemSetConfig
        });
    },


    createFormItemComponent: function (inputConfig, value) {
        var me = this;
        var classAlias = 'widget.' + inputConfig.type.name;

        return Ext.create({
            xclass: classAlias,
            fieldLabel: inputConfig.label,
            name: inputConfig.name,
            inputConfig: inputConfig,
            value: value || ''
        });

    },

    getCtyConfigForContentItem: function (contentItem, root) {
        var node, name;
        for (var key in root) {
            node = root[key];

            if (node.FormItemSet) {
                name = node.FormItemSet.name;
            } else { // Input
                name = node.Input.name;
            }

            if (name === contentItem.name) {
                return node;
            }
        }

        return null;
    },
    formItemIsSupported: function (classAlias) {
        return Ext.ClassManager.getByAlias(classAlias);
    }

});