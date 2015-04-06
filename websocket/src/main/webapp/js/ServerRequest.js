ServerRequest = {};

ServerRequest._send = function (no, obj) {
    ws.send(JSON.stringify({no: no, data: JSON.stringify(obj)}));
};

/**
 * 匿名登陆
 * @param pid    旧的pid,没有则为-1
 */
ServerRequest.AnonymousLogin = function (pid) {
    this._send(0, {pid:pid});
};

/**
 * 加入桌子
 * @param tableId    桌号，默认-1* @param pid    玩家id
 */
ServerRequest.JoinTable = function (tableId,pid) {
    this._send(1, {tableId:tableId,pid:pid});
};

/**
 * 举手
 * @param pid    用户id
 */
ServerRequest.RaiseHand = function (pid) {
    this._send(2, {pid:pid});
};

/**
 * 叫地主
 * @param pid    player id* @param isCall    1:call,0:give up
 */
ServerRequest.CallDealer = function (pid,isCall) {
    this._send(5, {pid:pid,isCall:isCall});
};

