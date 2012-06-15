AdminLiveEdit.Button = function()
{
    function createButton( config )
    {
        var text = config.text || '',
            iconCls = config.iconCls || '';

        var html = '<div class="live-edit-button"><button class="live-edit-button">';
        if ( iconCls !== '' ) {
            html += '<span class="live-edit-button-icon ' + iconCls + '"></span>';
        }
        html += '<span>'+ text +'</span></button></div>';

        var button = $liveedit( html );
        button.on('click', function() {
            config.handler.call(this);
        });

        return button;
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Public

    return {
        create: function( config ) {
            return createButton( config );
        }
    };

}();