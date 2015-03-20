/**
 * Created by landy on 15/3/20.
 */
var SushiSprite = cc.Sprite.extend({
    disappearAction: null,
    onEnter: function () {
        cc.log("onEnter...");
        this._super();
        this.addTouchEventListener();

        this.disappearAction = this.createDisappearAction();
        this.disappearAction.retain();
    },
    onExit: function () {
        cc.log("onExit...");
        this.disappearAction.release();
        this._super();
    },
    addTouchEventListener: function () {
        this.touchListener = cc.EventListener.create({
            event: cc.EventListener.TOUCH_ONE_BY_ONE,
            swallowTouches: true,
            onTouchBegan: function (touch, event) {
                var pos = touch.getLocation();
                var target = event.getCurrentTarget();
                if (cc.rectContainsPoint(target.getBoundingBox(), pos)) {

                    target.removeTouchEventListenser;
                    target.stopAllActions();
                    var ac = target.disappearAction;
                    var seqAc = cc.sequence(ac, cc.callFunc(function () {
                        target.removeFromParent();
                    }, target));

                    target.runAction(seqAc);

                    target.getParent().addScore();

                    return true;
                }
                return false;
            }
        });
        cc.eventManager.addListener(this.touchListener, this);
    },
    removeTouchEventListenser: function () {
        cc.eventManager.removeListener(this.touchListener);
    },
    createDisappearAction: function () {
        var frames = [];
        for (var i = 0; i < 11; i++) {
            var str = "sushi_1n_" + i + ".png"
            //cc.log(str);
            var frame = cc.spriteFrameCache.getSpriteFrame(str);
            frames.push(frame);
        }

        var animation = new cc.Animation(frames, 0.02);
        var action = new cc.Animate(animation);

        return action;
    }
});
