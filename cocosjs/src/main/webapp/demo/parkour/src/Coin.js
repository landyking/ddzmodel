/**
 * Created by landy on 2015/3/22.
 */
var Coin = cc.Class.extend({
    space: null,
    sprite: null,
    shape: null,
    _mapIndex: 0,
    get mapIndex() {
        return this._mapIndex;
    },
    set mapIndex(index) {
        this._mapIndex = index;
    },
    ctor: function (spriteSheet, space, pos) {
        this.space = space;

        var animaFrames = [];
        for (var i = 0; i < 8; i++) {
            var str = "coin" + i + ".png";
            var frame = cc.spriteFrameCache.getSpriteFrame(str);
            animaFrames.push(frame);
        }

        var animation = new cc.Animation(animaFrames, 0.2);
        var action = cc.repeatForever(new cc.Animate(animation));

        this.sprite = new cc.PhysicsSprite("#coin0.png");

        var radius = 0.95 * this.sprite.getContentSize().width / 2;
        var body = new cp.StaticBody();
        body.setPos(pos);
        this.sprite.setBody(body);

        this.shape = new cp.CircleShape(body, radius, cp.vzero);
        this.shape.setCollisionType(SpriteTag.coin);
        this.shape.setSensor(true);

        this.space.addStaticShape(this.shape);

        this.sprite.runAction(action);
        spriteSheet.addChild(this.sprite, 1);
    },
    removeFromParent: function () {
        this.space.removeStaticShape(this.shape);
        this.shape = null;
        this.sprite.removeFromParent();
        this.sprite = null;
    },
    getShape: function () {
        return this.shape;
    }
});