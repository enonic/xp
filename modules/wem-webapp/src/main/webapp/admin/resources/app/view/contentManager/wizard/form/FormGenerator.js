Ext.define('Admin.view.contentManager.wizard.form.FormGenerator', {

    addComponentsBasedOnContentType: function (contentTypeItemConfigItems, parentComponent, contentData) {
        var me = this;
        var component;

        Ext.each(contentTypeItemConfigItems, function (item) {
            var contentItemConfig = me.getContentItemConfig(item);
            var data = me.getDataForConfig(contentItemConfig, contentData);
            var creationFunction = me.constructCreationFunction(item);
            component = creationFunction.call(me, contentItemConfig, data);

            me.addComponent(component, parentComponent);
        });
    },


    /**
     * @private
     */
    addComponent: function (component, parentComponent) {
        if (this.componentIsContainer(parentComponent)) {
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
            value: contentItem
        });
    },

    /**
     * @private
     */
    createInputComponent: function (inputConfig, contentItem) {
        var classAlias = 'widget.' + inputConfig.type.name;
        if (!this.formItemIsSupported(classAlias)) {
            console.error('Unsupported input type', inputConfig);
            return;
        }
        var label = inputConfig.label;
        if (inputConfig.occurrences.minimum > 0) {
            label += ' <sup style="color: #E32400">' + inputConfig.occurrences.minimum + '</sup>';
        }
        return Ext.create({
            xclass: classAlias,
            fieldLabel: label,
            name: inputConfig.name,
            copyNo: inputConfig.copyNo || 1,
            contentTypeItemConfig: inputConfig,
            value: contentItem
        });

    },

    /**
     * @private
     */
    getDataForConfig: function (contentItemConfig, contentData) {
        var key, data = [];

        for (key in contentData) {
            if (contentData.hasOwnProperty(key)) {
                if (contentItemConfig.name === contentData[key].name) {
                    data.push(contentData[key]);
                }
            }
        }

        return data;
    },


    /**
     * Trying to find function that handles element creation.
     * Function name should be "create" + content type capitalized + "Component"
     * @private
     */
    constructCreationFunction: function (contentTypeConfig) {
        var key;

        for (key in contentTypeConfig) {
            if (contentTypeConfig.hasOwnProperty(key)) {
                return this["create" + key + "Component"];
            }
        }
        console.error("No handler for ", contentTypeConfig);
        return null;
    },

    /**
     *
     * @private
     */
    getContentItemConfig: function (contentTypeConfig) {
        var key;

        for (key in contentTypeConfig) {
            if (contentTypeConfig.hasOwnProperty(key)) {
                return contentTypeConfig[key];
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
        return component.getXType() === 'FormItemSet' || component.getXType() === 'FieldSetLayout' ||
               component.getXType() === 'fieldcontainer' || component.getXType() === 'container';
    }

});