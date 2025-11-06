export default class AxiosLoading {
  _target: any = null;
  count = 0;

  /**
   * 关闭Loading层实例
   * @param {*} _options
   */
  closeLoading(loading: boolean) {
    if (loading && this.count > 0) this.count--;
    if (this.count === 0) {
      this._target.close();
      this._target = null;
    }
  }
}
