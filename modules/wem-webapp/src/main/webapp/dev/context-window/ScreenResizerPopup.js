/**
 * TODO: Controller? :)
 */
Ext.define('Admin.view.ScreenResizerPopup', {
    extend: 'Ext.container.Container',
    alias: 'widget.screenResizerPopup',
    floating: true,
    modal: true,
    shadow: false,
    width: 170,
    height: 120,
    x: 50,
    y: 40,
    cls: 'screen-resizer-ct-main',
    padding: 10,

    opener: undefined,
    closeCallback: undefined,

    selectedColor: 'a4c400',
    colorPickerColors: [
        'a4c400', '60a917', '008a00', '00aba9',
        '1ba1e2', '0050ef', '6a00ff', 'aa00ff',
        'f472d0', 'd80073', 'a20025', 'e51400',
        'fa6800', 'f0a30a', 'e3c800', '825a2c',
        '6d8764', '647687', '76608a', '87794e'
    ],

    initComponent: function () {
        var me = this;
        this.items = [
            {
                xtype: 'form',
                bodyStyle: 'background: transparent',
                border: false,
                defaults: {
                    labelCls: 'screen-resizer-label',
                    labelWidth: 50
                },
                defaultType: 'textfield',
                items: [
                    {
                        xtype: 'colorpicker',
                        cls: 'screen-resizer-color-picker',
                        height: 40,
                        value: me.selectedColor,
                        colors: me.colorPickerColors,
                        listeners: {
                            select: function(picker, selColor) {
                                me.selectedColor = selColor;
                            }
                        }
                    },
                    {
                        fieldLabel: 'Name',
                        fieldCls: 'screen-resizer-input',
                        itemId: 'name',
                        name: 'name',
                        value: 'Untitled',
                        allowBlank: false,
                        listeners: {
                            specialkey: function(field, event){
                                if (event.getKey() == event.ENTER) {
                                    me.handleFormSubmit()
                                }
                            },
                            afterrender: function(field) {
                                field.focus(false, 100);
                            }
                        }
                    }
                ],
                buttons: [
                    {
                        text: 'Add',
                        formBind: true,
                        disabled: true,
                        cls: 'screen-resizer-button',
                        handler: function (btn) {
                            me.handleFormSubmit()
                        }
                    }
                ]

            }
        ];

        this.callParent(arguments);
    },

    handleFormSubmit: function () {
        var me = this;
        if (me.closeCallback) {
            me.closeCallback.call(me.opener, me.down('#name').getValue(), me.opener.resizerCmp.getEl().getWidth(), me.selectedColor);
        }
        me.destroy();

    },

    doShow: function () {
        this.show();
    }

});