const innerLib = require('inner-script');

console.log('I am a require function');

log.info("Print logs with two arguments: %s and %s ", 123.09, "456");
log.info('info message', new Error('cause'));

exports.get = function(req) {
    innerLib.myFunction();

    console.log('GET function is called');

    console.log(JSON.stringify(app, null, 2));
    console.log(JSON.stringify(xxx, null, 2));

    var bean = __.newBean('com.enonic.xp.script.graaljs.impl.GraalScripBean');
    bean.source = "Source Name 1";
    bean.execute();
    bean.execute(1);
    bean.execute([1,2,3]);
    bean.execute("Hello", 1234);

    var mockObj = __.registerMock('/lib/xp/something', {
        a: 1,
        b: 2
    });
    var mock = require('/lib/xp/something');
    console.log('Mock: ' + mock.a);
}