Ext.define('Admin.view.contentManager.wizard.form.input.Base', {
    extend: 'Ext.form.FieldContainer',
    label: '',

    contentTypeItemConfig: undefined,

    width: 1000,

    layout: {
        type: 'hbox'
    },

    mixins: {
        fieldOccurrencesHandler: 'Admin.view.contentManager.wizard.form.FieldOccurrencesHandler'
    },

    listeners: {
        afterrender: function () {
            this.handleOccurrences();
        }
    },


    initComponent: function () {
        var me = this;

        me.defaults = {
            margin: '0 0 5 5',
            width: 450
        };
        if (this.copyNo > this.contentTypeItemConfig.occurrences.minimum) {
            this.items.push(this.createDeleteButton());
        }
        me.callParent(arguments);

    },

    bindOccurrencesEventsHandlers: function () {
        this.on('copyadded', this.updateButtonState, this);
        this.on('copyremoved', this.updateButtonState, this);
    },

    getValue: function () {
        return {
            path: this.name.concat('[', this.copyNo - 1, ']'),
            value: this.items.items[0].getValue()
        };
    },

    setValue: function (value) {

    },

    /**
     * @private
     * @return {*}
     */
    createDeleteButton: function () {
        var element = this;
        return {
            xtype: 'button',
            mode: 'delete',
            itemId: 'delete-button',
            iconCls: 'icon-remove icon-2x',
            cls: 'nobg icon-button',
            scale: 'medium',
            width: '24',
            listeners: {
                click: function () {
                    var remainElement = element.removeCopy();
                    if (remainElement) {
                        remainElement.updateButtonState();
                    }
                }
            }
        };
    },

    /**
     * @private
     * @param disabled
     */
    setButtonDisabled: function (disabled) {
        var button = this.down('#add-delete-button');
        if (button) {
            button.setDisabled(disabled);
        }
    },

    /**
     * Update state of component buttons, they could be either delete buttons or add buttons
     */
    updateButtonState: function () {
        var totalCount = 1;
        var tmp = this;
        while (tmp.prevField) {
            tmp = tmp.prevField;
        }
        var root = tmp;
        while (tmp.nextField) {
            tmp = tmp.nextField;
            totalCount++;
        }
        root.updateButtonStateInternal(totalCount);
    },

    /**
     * @private
     * @param totalCount
     */
    updateButtonStateInternal: function (totalCount) {
        var min = this.contentTypeItemConfig.occurrences.minimum;
        var max = this.contentTypeItemConfig.occurrences.maximum;
        this.setButtonDisabled(totalCount === min && this.copyNo !== totalCount);
        if (this.nextField) {
            this.nextField.updateButtonStateInternal(totalCount);
        }
    }


})
;