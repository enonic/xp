Ext.define('Admin.view.AutosizeTextField', {
    extend: 'Ext.Component',
    alias: 'widget.autosizeTextField',

    margin: '0 20px 0 0',

    style: {
        overflow: 'hidden'
    },

    isEmpty: true,
    emptyText: '',

    fieldHeight: undefined,

    isMouseOver: false,
    isFocused: false,

    tpl: '<div class="autosizeTextField" style="' +
         'float: left; ' +
         'border: 1px solid #EEEEEE; ' +
         'min-width: 200px; ' +
         'padding: 0px 10px; ' +
         'margin: 3px; ' +
         'white-space: nowrap; ' +
         'overflow: hidden; ' +
         '" contenteditable="true">{value}' +
         '</div>',

    data: {
        value: ''
    },

    ons: undefined,

    initComponent: function () {
        this.data.value = this.value;

        this.isEmpty = !this.data.value;
        if (this.isEmpty) {
            this.data.value = this.emptyText;
        }

        this.callParent(arguments);
    },

    afterRender: function () {
        var me = this;

        me.callParent();

        var textEl = this.el.down('.autosizeTextField');
        textEl.on(this.ons);

        textEl.on({
            focus: function () {
                me.isFocused = true;
                if (me.isEmpty) {
                    me.setRawValue('');
                }
                me.updateComponent();
            },
            blur: function () {
                me.isFocused = false;
                me.isEmpty = !me.getRawValue();
                if (me.isEmpty) {
                    me.setRawValue(me.emptyText);
                }
                me.updateComponent();
            },
            mouseover: function () {
                me.isMouseOver = true;
                me.updateComponent();
            },
            mouseout: function () {
                me.isMouseOver = false;
                me.updateComponent();
            }
        });

        this.textEl = textEl;

        if (me.isEmpty) {
            me.setRawValue(me.emptyText);
        }

        this.updateComponent();
    },

    updateComponent: function () {
        if (!this.textEl) {
            return;
        }
        this.textEl.applyStyles({
            boxShadow: this.isFocused ? '0 0 3px #98C9F2' : 'none',
            border: '1px solid ' + (this.isMouseOver || this.isFocused ? '#98C9F2' : '#EEEEEE'),
            color: (this.isEmpty && !this.isFocused) ? '#555555' : 'black',
            fontSize: (this.fieldHeight - 14) + 'px',
            minHeight: (this.fieldHeight) + 'px'
        });
    },

    on: function (ons) {
        var me = this;

        var keyup = ons.keyup;

        ons.keyup = function (field, event, opts) {
            field.getValue = function () {
                return me.getValue();
            };
            keyup.call(ons.scope, field, event, opts);
        };

        ons.input = function (field, newVal, oldVal) {
            newVal = me.getRawValue();
            oldVal = me.getRawValue();
            ons.change.call(ons.scope, field, newVal, oldVal);
        };

        ons.keypress = function (event) {
            var value = String.fromCharCode(event.charCode);

            var newValue = 'ok';

            if (value != '' && event.charCode != 0) {
                newValue = value.replace(me.stripCharsRe, '');
            }

            if (event.keyCode == event.RETURN || newValue == '') {
                event.preventDefault();
            }

            this.isEmpty = false;
        };

        this.ons = ons;
    },

    getValue: function () {
        return this.isEmpty ? '' : this.getRawValue();
    },

    getRawValue: function () {
        return this.textEl ? this.textEl.dom.textContent : this.value;
    },

    setValue: function (value) {
        this.isEmpty = !value;
        this.setRawValue(value);
    },

    setRawValue: function (value) {
        if (this.textEl) {
            this.textEl.dom.textContent = value;
        } else {
            this.value = value;
        }
    },

    processRawValue: function (value) {
        var me = this,
            stripRe = me.stripCharsRe,
            newValue;

        if (stripRe) {
            newValue = value.replace(stripRe, '');
            if (newValue !== value) {
                me.setRawValue(newValue);
                value = newValue;
            }
        }
        return value;
    },

    getFocusEl: function () {
        return this.textEl;
    }

});