Ext.define('Admin.view.contentManager.wizard.form.FieldOccurrencesHandler', {

    copyNo: 1,

    /**
     * Handles multiple occurences, called right before component is rendered, could be overriden for custom implementation
     */
    handleOccurrences: function () {
        this.addEvents('copyadded', 'copyremoved');
        this.bindOccurrencesEventsHandlers();
        var minOcc = this.contentTypeItemConfig.occurrences.minimum;
        if (this.contentTypeItemConfig && (this.value === undefined) && (this.copyNo < minOcc)) {
            this.addCopy();
        } else {
            var value = this.value;
            if (value instanceof Array) {
                this.setValue(value[0].value);
                if (value.length > 1) {
                    this.addCopy(value.slice(1));
                }
            }
        }
        this.fireEvent('copyadded', this);
    },

    /**
     * Should be implemented in target class
     */
    bindOccurrencesEventsHandlers: function () {

    },

    /**
     * Adds copy of current component to the parent content.
     * @return copy {*|Ext.Component|Ext.Component}
     */
    addCopy: function (value) {
        var parent = this.up();

        var clone = this.cloneConfig({
            copyNo: this.copyNo + 1,
            value: value || ''
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
        this.fireEvent('copyadded', this);
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
        if (linkedField) {
            linkedField.updateCopyNo();
            linkedField.fireEvent('copyremoved', linkedField);
        }

        return linkedField;
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
    }
});