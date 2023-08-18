'use strict'
const target = process.env.npm_lifecycle_event;
console.log('env is deploying, current env is', target);

module.exports = {
  NODE_ENV: '"production"'
}

/*module.exports = {
  NODE_ENV: '"production"'
}*/


