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

    showAddDeleteButton: true,

    listeners: {
        beforerender: function () {
            this.handleOccurrences();
        }
    },


    initComponent: function () {
        var me = this;

        me.defaults = {
            margin: '0 0 5 5',
            width: 450
        };
        if (this.contentTypeItemConfig.occurrences.minimum !== this.contentTypeItemConfig.occurrences.maximum && this.showAddDeleteButton) {
            this.items.push(this.createAddDeleteButton());
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
    createAddDeleteButton: function () {
        var element = this;
        return {
            xtype: 'button',
            ui: 'grey',
            mode: 'delete',
            itemId: 'add-delete-button',
            text: 'x',
            width: 50,
            listeners: {
                click: function () {
                    switch (this.mode) {
                    case 'add':
                        element.addCopy();
                        element.updateButtonState();
                        break;
                    case 'delete':
                        var remainElement = element.removeCopy();
                        if (remainElement) {
                            remainElement.updateButtonState();
                        }
                        break;
                    }
                }
            },
            changeMode: function (mode) {
                if (mode) {
                    this.setText('+');
                    this.mode = 'add';
                } else {
                    this.setText('x');
                    this.mode = 'delete';
                }
            }
        };
    },

    /**
     * @private
     * @param state - true is for add mode, false is for delete mode
     */
    setButtonState: function (state) {
        var button = this.down('#add-delete-button');
        if (button) {
            button.changeMode(state);
        }
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
     * Update state of component buttons, they could either delete buttons or add buttons
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
        if (this.copyNo === max) {
            this.setButtonState(false);
        } else if (this.copyNo === totalCount) {
            this.setButtonState(true);
        } else {
            this.setButtonState(false);
        }
        if (this.nextField) {
            this.nextField.updateButtonStateInternal(totalCount);
        }
    }


});