var assert = Java.type('org.junit.Assert');

var result = execute2('test.command', {
    name: 'scripting',
    transform: function (str) {
        return str + " is cool";
    }
});

assert.assertEquals('scripting is cool', result);

