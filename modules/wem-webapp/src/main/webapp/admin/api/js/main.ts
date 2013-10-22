/**
 * Main file for all admin API classes and methods.
 */

// require ExtJs as long as it is used for implementation
///<reference path='lib/ExtJs.d.ts' />

///<reference path='plugin/_module.ts' />
///<reference path='plugin/fileupload/_module.ts' />


///<reference path='lib/Mousetrap.d.ts' />
///<reference path='lib/jquery.d.ts' />
///<reference path='lib/jqueryui.d.ts' />
///<reference path='lib/codemirror.d.ts' />
///<reference path='lib/slickgrid.d.ts' />
///<reference path='lib/slickgrid-plugins.d.ts' />
///<reference path='lib/hasher.d.ts' />

///<reference path='util/_module.ts' />

///<reference path='model/_module.ts' />

///<reference path='handler/_module.ts' />

///<reference path='remote/_module.ts' />
///<reference path='remote/account/_module.ts' />
///<reference path='remote/content/_module.ts' />
///<reference path='remote/contenttype/_module.ts' />
///<reference path='remote/mixin/_module.ts' />
///<reference path='remote/relationshiptype/_module.ts' />
///<reference path='remote/schema/_module.ts' />
///<reference path='remote/space/_module.ts' />
///<reference path='remote/userstore/_module.ts' />
///<reference path='remote/util/_module.ts' />

///<reference path='notify/_module.ts' />

///<reference path='event/_module.ts' />

///<reference path='rest/_module.ts' />

///<reference path='dom/_module.ts' />

///<reference path='ui/_module.ts' />
///<reference path='ui/combobox/_module.ts' />
///<reference path='ui/dialog/_module.ts' />
///<reference path='ui/form/_module.ts' />
///<reference path='ui/grid/_module.ts' />
///<reference path='ui/menu/_module.ts' />
///<reference path='ui/tab/_module.ts' />
///<reference path='ui/toolbar/_module.ts' />

///<reference path='facet/_module.ts' />

///<reference path='data/json/_module.ts' />
///<reference path='data/_module.ts' />

///<reference path='form/json/_module.ts' />
///<reference path='form/_module.ts' />
///<reference path='form/input/_module.ts' />
///<reference path='form/input/type/_module.ts' />
///<reference path='form/formitemset/_module.ts' />
///<reference path='form/layout/_module.ts' />


///<reference path='node/_module.ts' />
///<reference path='item/_module.ts' />

///<reference path='content/json/_module.ts' />
///<reference path='content/_module.ts' />

///<reference path='schema/_module.ts' />

///<reference path='schema/content/json/_module.ts' />
///<reference path='schema/content/_module.ts' />
///<reference path='schema/mixin/_module.ts'/>
///<reference path='schema/mixin/json/_module.ts'/>
///<reference path='schema/relationshiptype/_module.ts' />
///<reference path='schema/relationshiptype/json/_module.ts' />

///<reference path='page/json/_module.ts' />
///<reference path='page/_module.ts' />

///<reference path='app/_module.ts' />
///<reference path='app/browse/_module.ts' />
///<reference path='app/browse/filter/_module.ts' />
///<reference path='app/browse/grid/_module.ts' />
///<reference path='app/browse/grid2/_module.ts' />
///<reference path='app/delete/_module.ts' />
///<reference path='app/view/_module.ts' />
///<reference path='app/wizard/_module.ts' />


declare var Mousetrap;
declare var Ext;
declare var Admin;

Ext.Loader.setConfig({
    enabled: false,
    disableCaching: false
});

Ext.override(Ext.LoadMask, {
    floating: {
        shadow: false
    },
    msg: undefined,
    cls: 'admin-load-mask',
    msgCls: 'admin-load-text',
    maskCls: 'admin-mask-white'
});

