Ext.define( 'Lib.plugin.fileupload.PhotoUploadWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.photoUploadWindow',
    modal: true,
    title: 'Photo Upload Window',
    width: 250,
    height: 332,
    resizable: false,
    bodyStyle: {
        background: '#fff',
        padding: '10px'
    },

    listeners: {
        resize: function() {
            var slider = this.down('slider');
            var frameWidth = this.getFrame().getWidth();
            slider.setWidth(frameWidth);
        }
    },

    imageInitialWidth: null,
    imageInitialHeight: null,

    initComponent: function() {
        var me = this;

        var uploadButton = {
            xtype: 'filefield',
            name: 'photo',
            buttonOnly: true,
            msgTarget: 'side',
            allowBlank: false,
            anchor: '100%',
            buttonText: 'Choose',
            listeners: {
                change: function() {
                    me.onChangePhoto();
                }
            }
        };

        var cancelButton = {
            xtype: 'button',
            text: 'Cancel',
            handler: function() { me.close() }
        };

        var setButton = {
            xtype: 'button',
            text: 'Set',
            handler: function() { me.set() }
        };

        var previewContainer = {
            xtype: 'component',
            itemId: 'preview-frame',
            autoEl: {
                tag: 'div',
                style: {
                    border: '1px solid #ccc',
                    height: '235px',
                    overflow: 'hidden',
                    position: 'relative'
                },
                children: [
                    {
                        tag: 'img',
                        src: '',
                        style: {
                            position: 'absolute',
                            visibility: 'hidden',
                            cursor: 'move'
                        }
                    }
                ]
            }
        };

        var slider = {
            xtype: 'slider',
            value: 100,
            minValue: 0,
            maxValue: 100,
            useTips: false,
            listeners: {
                change: function(slider, newValue) {
                    var image = me.getImage();

                    var newWidth = me.imageInitialWidth * newValue / 100;
                    image.setWidth(newWidth);

                    var newHeight = me.imageInitialHeight * (image.getWidth() / me.imageInitialWidth);
                    image.setHeight(newHeight);
                }
            }
        };

        this.tbar = [
            uploadButton, '->', cancelButton, setButton
        ];

        this.items = [
            previewContainer,
            slider
        ];

        this.callParent( arguments );
    },

    onChangePhoto: function() {
        // TODO: Break up
        var me = this;
        var file = me.getSelectedFiles()[0];
        var previewImage = me.getImage();
        previewImage.on('load', function() {
            me.displayImage(true);
            me.setInitialPhotoSize();
            me.centerImage();

            var previewImageWidth = previewImage.getWidth();
            var frameWidth = me.getFrame().getWidth();
            var sliderNewMinVal = Math.ceil(previewImageWidth * frameWidth / 100 / 10);
            me.getSlider().setMinValue(sliderNewMinVal);

            // TODO: Instantiate only once
            var dd = new Ext.dd.DD(previewImage.dom.id, 'carsDDGroup', {
                isTarget  : false,
                moveOnly: true,
                maintainOffset: false,
                scroll: false
            });
            dd.onDrag = function(){
                dd.resetConstraints();
            };
        });
        previewImage.set({src: window.URL.createObjectURL(file)});
    },

    setInitialPhotoSize: function() {
        var frame = this.getFrame();
        var image = this.getImage();

        var fw = frame.getWidth();
        var fh = frame.getHeight();
        var iw = image.getWidth();
        var ih = image.getHeight();

        image.setWidth('');
        image.setHeight('');

        if ( iw > fw && ih > fh )
        {
            if ( ih > iw )
                image.setWidth(frame.getWidth() + 20);
            else
                image.setHeight(frame.getHeight() + 20);
        }
        else  if ( ih > fh )
        {
            image.setHeight(frame.getHeight());
        }
        else
        {
            image.setWidth(frame.getWidth());
        }

        this.imageInitialWidth = image.getWidth();
        this.imageInitialHeight = image.getHeight();
    },

    centerImage: function() {
        var frame = this.getFrame();
        var image = this.getImage();

        var frameWidth = frame.getWidth();
        var frameHeight = frame.getWidth();

        var imageWidth = image.getWidth();
        var imageHeight = image.getHeight();

        var imageCenterX = (frameWidth - imageWidth) / 2 - 2; // -2 for frame border left/right
        var imageCenterY = (frameHeight - imageHeight) / 2 - 2; // -2 for frame border left/right

        image.setLeft( imageCenterX + 'px' );
        image.setTop( imageCenterY + 'px' );
    },

    getFrame: function () {
        return this.down('#preview-frame');
    },

    getImage: function () {
        return this.getFrame().getEl().down('img');
    },

    getSelectedFiles: function () {
        return this.getEl().down('input[type=file]' ).dom.files;
    },

    getSlider: function () {
        return this.down('slider');
    },

    displayImage: function(show) {
        if ( show )
            this.getImage().show();
        else
            this.getImage().hide();
    },

    set: function() {
        this.close();
    },

    open: function() {
        this.show();
    }

});
