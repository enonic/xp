Ext.define( 'Admin.controller.userstore.BrowseToolbarController', {
    extend:'Admin.controller.userstore.Controller',

    /*      Controller for handling Toolbar UI events       */

    stores:[
    ],
    models:[
    ],
    views:[
        'Admin.view.userstore.DeleteUserstoreWindow'
    ],

    init:function ()
    {

        this.control( {
            'browseToolbar *[action=deleteUserstore]':{
                click:function ()
                {
                    this.showDeleteUserstoreWindow();
                }
            }
        } );
    }

} );
