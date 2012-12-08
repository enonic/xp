Ext.define('Admin.lib.formitem.FormHelper', {

    // Add components based on content.
    addComponentsForEditForm: function (content, contentTypeDef, parentComponent) {
        var me = this,
            config,
            component;

        Ext.Array.each(content.data, function(contentItem) {

            config = me._getConfigForContentItem(contentItem, contentTypeDef.form);

            if (config.FormItemSet) {
                component = me._createFormItemSet(config.FormItemSet);
            } else { // Input
                var classAlias = 'widget.' + config.Input.type.name;
                if (!me._formItemIsSupported(classAlias)) {
                    console.error('Unsupported input type', config.Input);
                    return;
                }
                component = me._createFormItemComponent(config.Input, contentItem.value);
            }

            me._addComponent(component, parentComponent);
        });
    },


    // Add components based on content type configuration.
    // Refactor this as it is shared by wizard.ContentDataPanel and lib.formitem.FormItemSet
    // FormItemSet may use it's own method for adding components.
    addComponentsForNewForm: function (contentTypeItems, parentComponent) {
        var me = this;
        var component;

        Ext.each(contentTypeItems, function (item) {
            if (item.FormItemSet) {
                component = me._createFormItemSet(item.FormItemSet);
            } else { // Input
                var classAlias = 'widget.' + item.Input.type.name;
                if (!me._formItemIsSupported(classAlias)) {
                    console.error('Unsupported input type', item.Input);
                    return;
                }

                component = me._createFormItemComponent(item.Input);
            }

            me._addComponent(component, parentComponent);
        });
    },


    _addComponent: function (component, parentComponent) {
        if (parentComponent.getXType() === 'FormItemSet' || parentComponent.getXType() === 'fieldcontainer' || parentComponent.getXType() === 'container') {
            parentComponent.add(component);
        } else {
            parentComponent.items.push(component);
        }
    },


    _createFormItemSet: function (formItemSetConfig, dataValue) {
        return Ext.create({
            xclass: 'widget.FormItemSet',
            name: formItemSetConfig.name,
            formItemSetConfig: formItemSetConfig
        });
    },


    _createFormItemComponent: function (inputConfig, value) {
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


    _getConfigForContentItem: function (contentItem, contentTypeConfig) {
        var node, name;

        for (var key in contentTypeConfig) {
            node = contentTypeConfig[key];

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


    _formItemIsSupported: function (classAlias) {
        return Ext.ClassManager.getByAlias(classAlias);
    }

});