Ext.define('Admin.view.contentManager.wizard.form.FormGenerator', {

    addComponentsBasedOnContentType: function (formItemConfigs, parentComponent, dataSet) {
        var me = this;
        var component;

        Ext.each(formItemConfigs, function (item) {
            var formItemConfig = me.getFormItemConfig(item);
            var data = me.getDataForConfig(formItemConfig, dataSet);
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
        var formItemSetComponent = Ext.create({
            xclass: 'widget.FormItemSet',
            name: formItemSetConfig.name,
            formItemSetConfig: formItemSetConfig,
            value: formItemSetData
        });
        return Ext.create({
            xclass: 'widget.formItemSetContainer',
            field: formItemSetComponent
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

        var inputComponent = Ext.create({
            xclass: classAlias,
            name: inputConfig.name,
            copyNo: inputConfig.copyNo || 1,
            inputConfig: inputConfig,
            value: inputData
        });

        if (!inputComponent.defaultOccurrencesHandling) {
            inputComponent.setFieldLabel(this.generateLabelHTML(inputConfig));
            return inputComponent;
        } else {
            var fieldLabel = this.createInputLabel(inputConfig);
            return Ext.create({
                xclass: 'widget.inputContainer',
                label: fieldLabel,
                field: inputComponent
            });
        }

    },

    /**
     * @private
     * @param inputConfig
     */
    createInputLabel: function (inputConfig) {
        var label = this.generateLabelHTML(inputConfig);
        label += ':';
        return Ext.create({
            xclass: 'widget.label',
            width: 110, // TODO fix this. Should be the same as in input.Base labelWidth. Now there is an extra padding.
            styleHtmlContent: true,
            html: label
        });
    },

    generateLabelHTML: function (inputConfig) {
        var label = inputConfig.label;
        if (inputConfig.occurrences.minimum > 0) {
            var requiredTitle = "Minimum " + inputConfig.occurrences.minimum + ' ' +
                                (inputConfig.occurrences.minimum == 1 ? 'occurrence is' : 'occurrences are') + ' required';
            label += ' <sup style="color: #E32400" title="' + requiredTitle + '">*</sup>';
        }

        return label;
    },

    /**
     * @private
     */
    getDataForConfig: function (formItemConfig, dataSet) {
        var key, data = [];

        for (key in dataSet) {
            if (dataSet.hasOwnProperty(key)) {
                if (formItemConfig.name === dataSet[key].name) {
                    data.push(dataSet[key]);
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