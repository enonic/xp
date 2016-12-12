const isCI = require('is-ci');

module.exports = function ci() {
    const code = isCI ? 1 : 0;

    if (isCI) {
        console.log('CI detected.');
    } else {
        console.log('CI not detected.');
    }

    process.exit(code);
};
