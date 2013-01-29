Ext.define('Admin.view.contentManager.wizard.form.input.Base', {
    extend: 'Ext.form.FieldContainer',
    label: '',

    contentTypeItemConfig: undefined,

    copyNo: 1,

    width: 1000,

    layout: {
        type: 'hbox'
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

    getValue: function () {
        return {
            path: this.name.concat('[', this.copyNo - 1, ']'),
            value: this.items.items[0].getValue()
        };
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
     * Handles multiple occurences, called right before component is rendered, could be overriden for custom implementation
     */
    handleOccurrences: function () {
        var minOcc = this.contentTypeItemConfig.occurrences.minimum;
        if (this.contentTypeItemConfig && (this.value === undefined) && (this.copyNo < minOcc)) {
            this.addCopy();
        }
        this.updateButtonState();
    },

    /**
     * Adds copy of current component to the parent content.
     * @return copy {*|Ext.Component|Ext.Component}
     */
    addCopy: function () {
        var parent = this.up();

        var clone = this.cloneConfig({
            copyNo: this.copyNo + 1
        });
        //Support links between copies (linked list), so we could analyze them and change their state
        this.nextField = clone;
        clone.prevField = this;
        var me = this;
        var index = parent.items.findIndexBy(function (item) {
            if (item.getItemId() === me.getItemId()) {
                return true;
            }
            return false;
        });
        parent.insert(index + 1, clone);
        return clone;
    },

    /**
     * Remove copy from parent content
     * @return one of the remain copies {*}
     */
    removeCopy: function () {
        var parent = this.up();
        var linkedField = this.prevField || this.nextField;
        // Set links to apropriate values
        if (this.prevField) {
            this.prevField.nextField = this.nextField;
        }
        if (this.nextField) {
            this.nextField.prevField = this.prevField;
        }
        parent.remove(this);
        return linkedField;
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
        root.updateCopyNo();
        root.updateButtonStateInternal(totalCount);
    },

    /**
     * @private
     */
    updateCopyNo: function () {
        if (this.prevField) {
            this.copyNo = this.prevField.copyNo + 1;
        } else {
            this.copyNo = 1;
        }
        if (this.nextField) {
            this.nextField.updateCopyNo();
        }
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