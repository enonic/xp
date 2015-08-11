var result = execute('test.command', {
    name: 'scripting',
    transform: function (str) {
        return str + " is cool";
    }
});

assert.assertEquals('scripting is cool', result);
