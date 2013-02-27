Ext.define('Admin.view.contentManager.wizard.form.FormGenerator', {

    addComponentsBasedOnContentType: function (formItemConfigs, parentComponent, contentData) {
        var me = this;
        var component;

        Ext.each(formItemConfigs, function (item) {
            var formItemConfig = me.getFormItemConfig(item);
            var data = me.getDataForConfig(formItemConfig, contentData);
            var creationFunction = me.constructCreationFunction(item);
            component = creationFunction.call(me, formItemConfig, data);

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
    createLayoutComponent: function (fieldSetLayoutConfig, fieldSetLayoutData) {
        return Ext.create({
            xclass: 'widget.FieldSetLayout',
            name: fieldSetLayoutConfig.name,
            fieldSetLayoutConfig: fieldSetLayoutConfig,
            content: fieldSetLayoutData
        });
    },


    /**
     * @private
     */
    createFormItemSetComponent: function (formItemSetConfig, formItemSetData) {
        return Ext.create({
            xclass: 'widget.FormItemSet',
            name: formItemSetConfig.name,
            formItemSetConfig: formItemSetConfig,
            value: formItemSetData
        });
    },

    /**
     * @private
     */
    createInputComponent: function (inputConfig, inputData) {
        var classAlias = 'widget.' + inputConfig.type.name;
        if (!this.formItemIsSupported(classAlias)) {
            console.error('Unsupported input type', inputConfig);
            return;
        }

        var field = Ext.create({
            xclass: classAlias,
            name: inputConfig.name,
            copyNo: inputConfig.copyNo || 1,
            inputConfig: inputConfig,
            value: inputData
        });
        return Ext.create({
            xclass: 'widget.inputContainer',
            label: this.createInputLabel(inputConfig),
            field: field
        });

    },

    /**
     * @private
     * @param inputConfig
     */
    createInputLabel: function (inputConfig) {
        var label = inputConfig.label + ':';
        if (inputConfig.occurrences.minimum > 0) {
            label += ' <sup style="color: #E32400">' + inputConfig.occurrences.minimum + '</sup>';
        }
        return Ext.create({
            xclass: 'widget.label',
            width: 100,
            styleHtmlContent: true,
            html: label
        });
    },

    /**
     * @private
     */
    getDataForConfig: function (formItemConfig, formItemData) {
        var key, data = [];

        for (key in formItemData) {
            if (formItemData.hasOwnProperty(key)) {
                if (formItemConfig.name === formItemData[key].name) {
                    data.push(formItemData[key]);
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
    constructCreationFunction: function (formItemConfig) {
        var key;

        for (key in formItemConfig) {
            if (formItemConfig.hasOwnProperty(key)) {
                return this["create" + key + "Component"];
            }
        }
        console.error("No handler for ", formItemConfig);
        return null;
    },

    /**
     *
     * @private
     */
    getFormItemConfig: function (formItemConfig) {
        var key;

        for (key in formItemConfig) {
            if (formItemConfig.hasOwnProperty(key)) {
                return formItemConfig[key];
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