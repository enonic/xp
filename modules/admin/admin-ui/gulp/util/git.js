const simpleGit = require('simple-git');
const path = require('path');

const root = path.resolve(__dirname, '../..');
const git = simpleGit(root);

function filterByExtension(files, extension) {
    if (extension) {
        return files.filter((file) => {
            const ext = path.parse(file).ext;
            return ext === extension;
        });
    }

    return files.slice(0);
}

// git status
function status(extension = '') {
    return new Promise((resolve) => {
        git.status((err, result) => {
            const files = result.files.map((file) => {
                return file.path;
            });

            resolve(filterByExtension(files, extension));
        });
    });
}

// git diff --name-only origin/master..HEAD
//   Outputs modified files from all commits that are not in master yet
function diff(extension = '') {
    const options = ["--name-only", "origin/master..HEAD"];
    return new Promise((resolve) => {
        git.diff(options, (err, result) => {
            const files = !result ? [] : result.trim().split('\n');

            resolve(filterByExtension(files, extension));
        });
    });
}


module.exports = {
    status,
    diff
};