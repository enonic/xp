Ext.define('Admin.view.contentManager.wizard.form.FormGenerator', {

    addComponentsBasedOnContentData: function (contentData, contentTypeItemConfig, parentComponent) {
        var me = this,
            config,
            component;

        Ext.Array.each(contentData, function(contentItem) {

            config = me.getConfigForContentItem(contentItem, contentTypeItemConfig);

            if (config.FormItemSet) {
                component = me.createFormItemSetComponent(config.FormItemSet, contentItem);
            } else if (config.Layout) {
                component = me.createLayoutComponent(config.Layout, contentItem);
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


    // Refactor this as it is shared between wizard.ContentDataPanel and lib.formitem.FormItemSet
    // FormItemSet may use it's own method for adding components.
    addComponentsBasedOnContentType: function (contentTypeItemConfigItems, parentComponent) {
        var me = this;
        var component;

        Ext.each(contentTypeItemConfigItems, function (item) {
            // Code smell: Refactor / clean up this.
            if (item.FormItemSet) {
                component = me.createFormItemSetComponent(item.FormItemSet);
            } else if (item.Layout && item.Layout.type === 'FieldSet') {
                component = me.createLayoutComponent(item.Layout);

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


    /**
     * @private
     */
    addComponent: function (component, parentComponent) {
        var me = this;
        if (me.componentIsContainer(parentComponent)) {
            parentComponent.add(component);
        } else {
            parentComponent.items.push(component);
        }
    },


    /**
     * @private
     */
    createLayoutComponent: function (layoutConfig, contentItem) {
        return Ext.create({
            xclass: 'widget.FieldSetLayout',
            name: layoutConfig.name,
            contentTypeItemConfig: layoutConfig,
            content: contentItem
        });
    },


    /**
     * @private
     */
    createFormItemSetComponent: function (formItemSetConfig, contentItem) {
        return Ext.create({
            xclass: 'widget.FormItemSet',
            name: formItemSetConfig.name,
            contentTypeItemConfig: formItemSetConfig,
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
            contentTypeItemConfig: inputConfig,
            value: value || ''
        });

    },


    /**
     * @private
     */
    getConfigForContentItem: function (contentItem, contentTypeConfig) {
        var node, name, key;

        for (key in contentTypeConfig) {
            if (contentTypeConfig.hasOwnProperty(key)) {
                node = contentTypeConfig[key];

                if (node.FormItemSet) {
                    name = node.FormItemSet.name;
                } else if (node.Layout) {
                    name = node.Layout.name;
                } else { // Input
                    name = node.Input.name;
                }

                if (name === contentItem.name) {
                    return node;
                }
            }
        }

        return null;
    },


    /**
     * @private
     */
    formItemIsSupported: function (classAlias) {
        return Ext.ClassManager.getByAlias(classAlias);
    },


    /**
     * @private
     */
    componentIsContainer: function (component) {
        return component.getXType() === 'FormItemSet' || component.getXType() === 'FieldSetLayout' || component.getXType() === 'fieldcontainer' || component.getXType() === 'container';
    }

});