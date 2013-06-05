module app_ui {

    export class WizardLayout {
        ext;

        constructor(animation: string = 'slide') {
            var cardLayout = new Ext.layout.container.Card({
                mixins: ['Ext.util.Animate'],
                deferredRender: false,
                renderHidden: false,
                animation: animation,
                easing: 'easeOut',
                duration: 500,

                setActiveItem: (item) => {
                    return this.setActiveItem(item);
                }
            });
            // cardLayout.self.mixin('animateMixin', Ext.util.Animate);

            this.ext = cardLayout;
        }

        private setActiveItem(item:any): any {
            var me = this.ext;
            var owner = me.owner;
            var oldCard = me.activeItem;
            var oldIndex = owner.items.indexOf(oldCard);
            var newCard = me.parseActiveItem(item);
            var newIndex = owner.items.indexOf(newCard);

            if (oldCard !== newCard) {

                owner.fireEvent("animationstarted", newCard, oldCard);

                if (newCard.rendered && me.animation && me.animation !== "none") {

                    me.syncFx();

                    var target = me.getRenderTarget();
                    newCard.setWidth(target.getWidth() - target.getPadding("lr") - Ext.getScrollbarSize().width);

                    switch (me.animation) {
                        case 'fade':
                            newCard.el.setStyle({
                                position: 'absolute',
                                opacity: 0,
                                top: me.getRenderTarget().getPadding('t') + 'px'
                            });
                            newCard.show();

                            if (oldCard) {
                                oldCard.el.fadeOut({
                                    useDisplay: true,
                                    duration: me.duration,
                                    callback: function () {
                                        me.hide();
                                    },
                                    scope: me.activeItem
                                });
                            }
                            owner.doLayout();
                            newCard.el.fadeIn({
                                useDisplay: true,
                                duration: me.duration,
                                callback: function () {
                                    newCard.el.setStyle({
                                        position: ''
                                    });
                                    owner.fireEvent("animationfinished", newCard, oldCard);
                                },
                                scope: this
                            });
                            break;
                        case 'slide':
                            newCard.el.setStyle({
                                position: 'absolute',
                                visibility: 'hidden',
                                width: me.getRenderTarget().getWidth(),
                                top: me.getRenderTarget().getPadding('t') + 'px'
                            });
                            newCard.show();

                            if (oldCard) {
                                oldCard.el.slideOut(
                                    newIndex > oldIndex ? "l" : "r",
                                    {
                                        duration: me.duration,
                                        easing: me.easing,
                                        remove: false,
                                        scope: me.activeItem,
                                        callback: function () {
                                            me.hide();
                                        }
                                    }
                                );
                            }

                            owner.doLayout();
                            newCard.el.slideIn(
                                newIndex > oldIndex ? "r" : "l",
                                {
                                    duration: me.duration,
                                    easing: me.easing,
                                    scope: me,
                                    callback: function () {
                                        newCard.el.setStyle({
                                            position: ''
                                        });
                                        owner.fireEvent("animationfinished", newCard, oldCard);
                                    }
                                }
                            );
                            break;
                    }

                    me.activeItem = newCard;
                    me.sequenceFx();

                } else {
                    me.callParent([newCard]);
                    owner.fireEvent("animationfinished", newCard, oldCard);
                }
                return newCard;
            }
        }

        /*
         // restrain item's width only to not exceed maxWidth
         // TODO: breaks the height if the item is smaller that container
         setItemBox : function( item, size ) {
         if( this.owner.restrainWidth ) {
         item.setWidth( Ext.Array.min( [ this.owner.restrainWidth, size.width ] ) )
         }
         }

         // default methods
         setItemBox : function(item, box) {
         var me = this;
         if (item && box.height > 0) {
         if (!me.owner.isFixedWidth()) {
         box.width = undefined;
         }
         if (!me.owner.isFixedHeight()) {
         box.height = undefined;
         }
         me.setItemSize(item, box.width, box.height);
         }
         },

         setItemSize: function(item, width, height) {
         if (Ext.isObject(width)) {
         height = width.height;
         width = width.width;
         }
         item.setCalculatedSize(width, height, this.owner);
         }
         */

    }
}

Ext.define('Admin.view.WizardLayout', {
    extend: 'Ext.layout.container.Card',
    alias: 'layout.wizard',
    mixins: ['Ext.util.Animate'],

    deferredRender: false,
    renderHidden: false,
    animation: 'slide',
    easing: 'easeOut',
    duration: 500,

    setActiveItem: function (item) {

        var owner = this.owner;
        var oldCard = this.activeItem;
        var oldIndex = owner.items.indexOf(oldCard);
        var newCard = this.parseActiveItem(item);
        var newIndex = owner.items.indexOf(newCard);

        if (oldCard !== newCard) {

            owner.fireEvent("animationstarted", newCard, oldCard);

            if (newCard.rendered && this.animation && this.animation !== "none") {

                this.syncFx();

                var target = this.getRenderTarget();
                newCard.setWidth(target.getWidth() - target.getPadding("lr") - Ext.getScrollbarSize().width);

                switch (this.animation) {
                    case 'fade':
                        newCard.el.setStyle({
                            position: 'absolute',
                            opacity: 0,
                            top: this.getRenderTarget().getPadding('t') + 'px'
                        });
                        newCard.show();

                        if (oldCard) {
                            oldCard.el.fadeOut({
                                useDisplay: true,
                                duration: this.duration,
                                callback: function () {
                                    this.hide();
                                },
                                scope: this.activeItem
                            });
                        }
                        owner.doLayout();
                        newCard.el.fadeIn({
                            useDisplay: true,
                            duration: this.duration,
                            callback: function () {
                                newCard.el.setStyle({
                                    position: ''
                                });
                                owner.fireEvent("animationfinished", newCard, oldCard);
                            },
                            scope: this
                        });
                        break;
                    case 'slide':
                        newCard.el.setStyle({
                            position: 'absolute',
                            visibility: 'hidden',
                            width: this.getRenderTarget().getWidth(),
                            top: this.getRenderTarget().getPadding('t') + 'px'
                        });
                        newCard.show();

                        if (oldCard) {
                            oldCard.el.slideOut(
                                newIndex > oldIndex ? "l" : "r",
                                {
                                    duration: this.duration,
                                    easing: this.easing,
                                    remove: false,
                                    scope: this.activeItem,
                                    callback: function () {
                                        this.hide();
                                    }
                                }
                            );
                        }

                        owner.doLayout();
                        newCard.el.slideIn(
                            newIndex > oldIndex ? "r" : "l",
                            {
                                duration: this.duration,
                                easing: this.easing,
                                scope: this,
                                callback: function () {
                                    newCard.el.setStyle({
                                        position: ''
                                    });
                                    owner.fireEvent("animationfinished", newCard, oldCard);
                                }
                            }
                        );
                        break;
                }

                this.activeItem = newCard;
                this.sequenceFx();

            } else {
                this.callParent([newCard]);
                owner.fireEvent("animationfinished", newCard, oldCard);
            }
            return newCard;
        }
    }

    /*
     // restrain item's width only to not exceed maxWidth
     // TODO: breaks the height if the item is smaller that container
     setItemBox : function( item, size ) {
     if( this.owner.restrainWidth ) {
     item.setWidth( Ext.Array.min( [ this.owner.restrainWidth, size.width ] ) )
     }
     }

     // default methods
     setItemBox : function(item, box) {
     var me = this;
     if (item && box.height > 0) {
     if (!me.owner.isFixedWidth()) {
     box.width = undefined;
     }
     if (!me.owner.isFixedHeight()) {
     box.height = undefined;
     }
     me.setItemSize(item, box.width, box.height);
     }
     },

     setItemSize: function(item, width, height) {
     if (Ext.isObject(width)) {
     height = width.height;
     width = width.width;
     }
     item.setCalculatedSize(width, height, this.owner);
     }
     */

});
