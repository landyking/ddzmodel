/**
 * Created by landy on 15/3/26.
 */
var Global = {};
Global.unitWidth = null;
Global.unitHeight = null;
Global.countWidth = 13;
Global.countHeight = 5;
Global._createSprite = function (val, color) {
    var pokerTexture = cc.textureCache.getTextureForKey(res.poker_png);
    var sprite = new cc.Sprite(pokerTexture,
        cc.rect(val * this.unitWidth, color * this.unitHeight, this.unitWidth, this.unitHeight));
    return sprite;
};
Global.createSpriteForCard = function (val) {
    if (-1 == val) {
        //未知牌
        return this._createSprite(2, 4);
    } else if (52 == val) {
        //小王
        return this._createSprite(0, 4);
    } else if (53 == val) {
        //大王
        return this._createSprite(1, 4);
    } else if (val > -1 && val < 52) {
        var color = val % 4;
        var cv = (val - color) / 4;
        return this._createSprite(cv, color);
    } else {
        throw new Error("未知的牌值" + val);
    }
};