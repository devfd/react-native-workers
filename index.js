
module.exports = {
  get self() {
    return require('./js/self').default;
  },
  get Worker() {
    return require('./js/worker').default;
  },
}
