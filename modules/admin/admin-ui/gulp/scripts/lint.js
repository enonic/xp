const notifier = require('node-notifier');
const git = require('../util/git');
const lint = require('../util/linter').lint;

function log(fails) {
    fails.forEach(fail => fail.log());

    if (fails.length > 0) {
        const title = 'Linting failed.';
        const message = 'Run "npm run prelint" to check again.';

        notifier.notify({title, message});
        console.error('\n' + title);
        console.error(message);

        process.exit(1);
    }
}

const diff = () => git.diff('.ts').then(lint).then(log);
const status = () => git.status('.ts').then(lint).then(log);

module.exports = {
    status,
    diff,
};