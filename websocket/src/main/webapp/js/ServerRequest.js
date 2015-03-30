ServerRequest = {};

ServerRequest._send = function (no, obj) {
    ws.send(JSON.stringify({no: no, data: JSON.stringify(obj)}));
};

/**
 * 匿名登陆
 * @param uid    旧的uid,没有则为-1
 */
ServerRequest.AnonymousLogin = function (uid) {
    this._send(0, {uid:uid});
};

/**
 * 加入桌子
 * @param uid    用户id
 */
ServerRequest.JoinTable = function (uid) {
    this._send(1, {uid:uid});
};

