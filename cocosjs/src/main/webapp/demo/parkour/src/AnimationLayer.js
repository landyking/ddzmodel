/**
 * Created by landy on 15/3/20.
 */
if (typeof RunnerStat == "undefined") {
    var RunnerStat = {};
    RunnerStat.running = 0;
    RunnerStat.jumpUp = 1;
    RunnerStat.jumpDown = 2;
}
;

var AnimationLayer = cc.Layer.extend({
    spriteSheet: null,
    runningAction: null,
    sprite: null,
    body: null,
    space: null,
    jumpUpAction: null,
    jumpDownAction: null,
    recognizer: null,
    stat: RunnerStat.running,

    ctor: function (space) {
        this._super();
        this.space = space;
        this.init();

        this._debugNode = new cc.PhysicsDebugNode(this.space);
        this._debugNode.setVisible(false);

        this.addChild(this._debugNode, 10);
    },
    init: function () {
        this._super();

        cc.spriteFrameCache.addSpriteFrames(res.runner_plist);
        this.spriteSheet = new cc.SpriteBatchNode(res.runner_png);
        this.addChild(this.spriteSheet);

        this.initAction();

        this.sprite = new cc.PhysicsSprite("#runner0.png");
        var contentSize = this.sprite.getContentSize();

        this.body = new cp.Body(1, cp.momentForBox(1, contentSize.width, contentSize.height));
        this.body.p = cc.p(g_runnerStartX, g_groundHight + contentSize.height / 2);
        this.body.applyImpulse(cp.v(150, 0), cp.v(0, 0));
        this.space.addBody(this.body);

        this.shape = new cp.BoxShape(this.body, contentSize.width - 14, contentSize.height);
        this.space.addShape(this.shape);

        this.sprite.setBody(this.body);
        this.sprite.runAction(this.runningAction);

        this.spriteSheet.addChild(this.sprite);

        this.recognizer = new SimpleRecognizer();
        cc.eventManager.addListener({
            event: cc.EventListener.TOUCH_ONE_BY_ONE,
            swallowTouches: true,
            onTouchBegan: this.onTouchBegan,
            onTouchMoved: this.onTouchMoved,
            onTouchEnded: this.onTouchEnded
        }, this);
        this.scheduleUpdate();
    },
    getEyeX: function () {
        return this.sprite.getPositionX() - g_runnerStartX;
    },
    update: function () {
        var statusLayer = this.getParent().getParent().getChildByTag(TagOfLayer.Status);
        statusLayer.updateMeter(this.sprite.getPositionX() - g_runnerStartX);

        var vel = this.body.getVel();
        if (this.stat == RunnerStat.jumpUp) {
            if (vel.y < 0.1) {
                this.stat = RunnerStat.jumpDown;
                cc.log("Change RunnerStat to jumpDown");
                this.sprite.stopAllActions();
                this.sprite.runAction(this.jumpDownAction);
            }
        } else if (this.stat == RunnerStat.jumpDown) {
            if (vel.y == 0) {
                this.stat = RunnerStat.running;
                cc.log("Change RunnerStat to running");
                this.sprite.stopAllActions();
                this.sprite.runAction(this.runningAction);
            }
        }
    },
    initAction: function () {
        var animFrames = [];
        for (var i = 0; i < 8; i++) {
            var str = "runner" + i + ".png";
            var frame = cc.spriteFrameCache.getSpriteFrame(str);
            animFrames.push(frame);
        }

        var animation = new cc.Animation(animFrames, 0.1);
        this.runningAction = cc.repeatForever(new cc.Animate(animation));
        this.runningAction.retain();

        animFrames = [];
        for (var i = 0; i < 4; i++) {
            var str = "runnerJumpUp" + i + ".png";
            var frame = cc.spriteFrameCache.getSpriteFrame(str);
            animFrames.push(frame);
        }

        animation = new cc.Animation(animFrames, 0.2);
        this.jumpUpAction = new cc.Animate(animation);
        this.jumpUpAction.retain();

        animFrames = [];
        for (var i = 0; i < 2; i++) {
            var str = "runnerJumpDown" + i + ".png";
            var frame = cc.spriteFrameCache.getSpriteFrame(str);
            animFrames.push(frame);
        }

        animation = new cc.Animation(animFrames, 0.3);
        this.jumpDownAction = new cc.Animate(animation);
        this.jumpDownAction.retain();
    },
    onTouchBegan: function (touch, event) {
        cc.log("touch began");
        /*var pos = touch.getLocation();
         event.getCurrentTarget().recognizer.beginPoint(pos.x, pos.y);*/
        return true;
    },
    onTouchMoved: function (touch, event) {
        cc.log("touch moved");
        /*  var pos = touch.getLocation();
         event.getCurrentTarget().recognizer.movePoint(pos.x, pos.y);*/
    },
    onTouchEnded: function (touch, event) {
        cc.log("touch ended");
        //var rtn = event.getCurrentTarget().recognizer.endPoint();
        //cc.log("rnt = " + rtn);
        //switch (rtn) {
        //    case "up":
        //        event.getCurrentTarget().jump();
        //        break;
        //    default:
        //        break;
        //}
        event.getCurrentTarget().jump();
    },
    jump: function () {
        cc.log("jump");
        if (this.stat == RunnerStat.running) {
            this.body.applyImpulse(cp.v(0, 250), cp.v(0, 0));
            this.stat = RunnerStat.jumpUp;
            cc.log("Change RunnerStat to jumpUp");
            this.sprite.stopAllActions();
            this.sprite.runAction(this.jumpUpAction);
        }
    },
    onExit: function () {
        this.runningAction.release();
        this.jumpUpAction.release();
        this.jumpDownAction.release();
        this._super();
    }
});