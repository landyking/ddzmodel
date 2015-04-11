/**
 * Created by landy on 15/3/26.
 */
var Global = {};
Global.pid=null;
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
Global.initCommonFunc = function () {
    if (cc) {
        cc.toast = function (node, text, time) {
            time = time || 1;
            var label = new cc.LabelTTF(text, "Arial", 21);
            var p = node.convertToNodeSpace(cc.p(cc.winSize.width / 2, cc.winSize.height / 2));
            label.setPosition(p);
            label.setColor(cc.color(0, 0, 0));
            label.setCascadeOpacityEnabled(true);
            label.setOpacity(0);
            node.addChild(label);
            label.runAction(cc.sequence(cc.fadeIn(1),cc.delayTime(time), cc.fadeOut(1), cc.callFunc(function () {
                label.removeFromParent();
            })));
        };
    }
};
Global.init = function () {
    var pkTexture = cc.textureCache.addImage(res.poker_png);
    Global.unitHeight = pkTexture.getContentSize().height / Global.countHeight;
    Global.unitWidth = pkTexture.getContentSize().width / Global.countWidth;

    this.initCommonFunc();
};