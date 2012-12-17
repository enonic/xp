Ext.define('Admin.lib.formitem.FormGenerator', {

    // Refactor this as it is shared between wizard.ContentDataPanel and lib.formitem.FormItemSet
    // FormItemSet may use it's own method for adding components.
    addComponentsBasedOnContentType: function (contentTypeItems, parentComponent) {
        var me = this;
        var component;

        Ext.each(contentTypeItems, function (item) {
            if (item.FormItemSet) {
                component = me.createFormItemSet(item.FormItemSet);
            } else { // Input
                var classAlias = 'widget.' + item.Input.type.name;
                if (!me.formItemIsSupported(classAlias)) {
                    console.error('Unsupported input type', item.Input);
                    return;
                }

                component = me.createFormItemComponent(item.Input);
            }

            me.addComponent(component, parentComponent);
        });
    },


    addComponentsBasedOnContentData: function (contentData, contentTypeConfig, parentComponent) {
        var me = this,
            config,
            component;

        // console.log('addComponentsForEditForm - contentData', contentData);

        Ext.Array.each(contentData, function(contentItem) {

            config = me.getConfigForContentItem(contentItem, contentTypeConfig);

            if (config.FormItemSet) {
                component = me.createFormItemSet(config.FormItemSet, contentItem);
            } else { // Input
                var classAlias = 'widget.' + config.Input.type.name;
                if (!me.formItemIsSupported(classAlias)) {
                    console.error('Unsupported input type', config.Input);
                    return;
                }
                component = me.createFormItemComponent(config.Input, contentItem.value);
            }

            me.addComponent(component, parentComponent);
        });
    },


    /**
     * @private
     */
    addComponent: function (component, parentComponent) {
        if (parentComponent.getXType() === 'FormItemSet' || parentComponent.getXType() === 'fieldcontainer' || parentComponent.getXType() === 'container') {
            parentComponent.add(component);
        } else {
            parentComponent.items.push(component);
        }
    },


    /**
     * @private
     */
    createFormItemSet: function (formItemSetConfig, contentItem) {
        return Ext.create({
            xclass: 'widget.FormItemSet',
            name: formItemSetConfig.name,
            formItemSetConfig: formItemSetConfig,
            content: contentItem
        });
    },


    /**
     * @private
     */
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


    /**
     * @private
     */
    getConfigForContentItem: function (contentItem, contentTypeConfig) {
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


    /**
     * @private
     */
    formItemIsSupported: function (classAlias) {
        return Ext.ClassManager.getByAlias(classAlias);
    }

});