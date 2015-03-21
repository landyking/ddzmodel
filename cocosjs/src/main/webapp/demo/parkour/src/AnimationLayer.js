/**
 * Created by landy on 15/3/20.
 */
var AnimationLayer = cc.Layer.extend({
    ctor: function () {
        this._super();
        this.init();
    },
    init: function () {
        this._super();

        var spriteRunner = new cc.Sprite(res.runner_png);
        spriteRunner.attr({x: 80, y: 85});

        var actionTo = cc.moveTo(2, cc.p(300, 85));
        spriteRunner.runAction(cc.sequence(actionTo));
        this.addChild(spriteRunner);
    }
});