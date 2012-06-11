Ext.define('App.view.LoggedInUserButton', {
    extend: 'Ext.Button',
    alias: 'widget.loggedInUserButton',
    text: 'No Name',
    menu: [],
    enableToggle: true,

    constructor: function(config)
    {
        config = config || {};
        config.listeners = config.listeners || {};

        Ext.applyIf(config.listeners, {
            move: {scope:this, fn:function(component, x, y, options) {
                this.updatePopupPosition(component, x, y, options);
            }}
        });

        // Can we use 'this' here instead of CMS.view ..?
        App.view.LoggedInUserButton.superclass.constructor.apply(this, arguments);
    },

    onRender: function()
    {
        // Can we use 'this' here instead of CMS.view ..?
        App.view.LoggedInUserButton.superclass.onRender.apply(this, arguments);

        this.createPopup();
    },

    toggleHandler: function(button, state)
    {
        state ? this.popup.show() : this.popup.hide();

        if ( state ) {
            var changeUserInput = Ext.get(Ext.DomQuery.selectNode('#main-change-user-input'));
            changeUserInput.dom.focus();
        }
    },

    createPopup: function()
    {
        var user = {
            uid: '2fcab58712467eab4004583eb8fb7f89',
            displayName: 'Morten Eriksen',
            qualifiedName: 'local\\mer',
            email: 'mer@enonic.com'
        };

        this.popup = Ext.create('Ext.Component', {
            width: 320,
            floating: true,
            cls: 'cms-logged-in-user-popup',
            bodyCls: 'cms-logged-in-user-popup-body',
            renderTo: Ext.getBody()
        });
        this.popup.hide();

        this.createPopupTemplate().overwrite(this.popup.getEl(), user);
        this.updatePopupPosition();
    },

    createPopupTemplate: function()
    {
        return new Ext.XTemplate( Templates.main.loggedInUserButtonPopup );
    },

    updatePopupPosition: function()
    {
        var buttonArea = this.getEl().getPageBox();
        var popupX = buttonArea.right - this.popup.width;
        var popupY = buttonArea.bottom;
        this.popup.setPosition(popupX, popupY);
    }

});

