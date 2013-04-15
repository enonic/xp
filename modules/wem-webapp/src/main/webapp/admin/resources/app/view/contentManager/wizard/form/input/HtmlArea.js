/*
 Note!
 Temporary HTML Area solution for 18/4
 Should not be used in production!
   * Does not respect inputConfig
   * Not tested with FormItemSet

 TODO:
 * Ta bort ekstra field label

 */

Ext.define('Admin.view.contentManager.wizard.form.input.HtmlArea', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.HtmlArea',

    initComponent: function() {
        var me = this;

        me.fieldLabel = me.inputConfig.label;

        me.items = [
            me.createContentEditableDiv(),
            me.createHiddenInput()
        ];

        me.callParent();

        me.getContentEditable().on('afterrender', function (component) {
            me.setContentEditableHtml(me.value.length > 0 ? me.value[0].value : '');
        });
    },


    getValue: function () {
        var me = this;

        me.copyContentEditableHtmlToHiddenDiv();

        var value = {
            path: me.name,
            value: me.getHiddenInput().getValue()
        };

        return value;
    },


    setValue: function (val) {
        this.getHiddenInput().setValue(val);
    },


    /**
     * @private
     */
    setContentEditableHtml: function (html) {
        var me = this,
            ce = me.getContentEditable();
        if (ce) {
            ce.getEl().setHTML(html);
        }
    },

    /**
     * @private
     */
    getContentEditable: function () {
        return this.down('#contentEditableDiv');
    },

    /**
     * @private
     */
    getHiddenInput: function () {
        return this.down('#' + this.name);
    },

    /**
     * @private
     */
    copyContentEditableHtmlToHiddenDiv: function () {
        var value = this.down('#contentEditableDiv').getEl().getHTML();
        this.down('#' + this.name).setValue(value);
    },

    /**
     * @private
     */
    createContentEditableDiv: function () {
        var me = this;
        return {
            xtype: 'component',
            width: 500,
            itemId: 'contentEditableDiv',
            cls: '.admin-html-area',
            layout:'vbox',
            autoEl: {
                tag: 'div',
                contenteditable: true
            },
            style: 'border: 1px solid #aaa; min-height: 100px; padding: 4px 9px',
            currentHeight: 100,
            listeners: {
                render: function (component) {
                    component.el.on('DOMSubtreeModified', function (event) {
                        var height = component.getHeight();

                        // Make sure doComponentLayout is only fired when the height changes
                        if (height !== component.currentHeight) {
                            component.currentHeight = height;
                            var parent = component.up();
                            if (Ext.isFunction(parent.doComponentLayout)) {
                                parent.doComponentLayout();
                            }
                        }
                    });
                }
            }
        };
    },

    /**
     * @private
     */
    createHiddenInput: function () {
        return {
            xtype: 'hiddenfield',
            name: this.name,
            itemId: this.name, // TODO: Is this unique enough?
            value: ''
        };
    }

});