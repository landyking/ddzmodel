/**
 * Created by landy on 15/3/20.
 */
var AnimationLayer = cc.Layer.extend({
    spriteSheet:null,
    runningAction:null,
    sprite:null,
    body:null,
    space:null,

    ctor: function (space) {
        this._super();
        this.space=space;
        this.init();

        this._debugNode = new cc.PhysicsDebugNode(this.space);
        this._debugNode.setVisible(false);

        this.addChild(this._debugNode,10);
    },
    init: function () {
        this._super();

        cc.spriteFrameCache.addSpriteFrames(res.runner_plist);
        this.spriteSheet = new cc.SpriteBatchNode(res.runner_png);
        this.addChild(this.spriteSheet);

        var animFrames=[];
        for(var i=0;i<8;i++) {
            var str = "runner" + i + ".png";
            var frame = cc.spriteFrameCache.getSpriteFrame(str);
            animFrames.push(frame);
        }
        var animation = new cc.Animation(animFrames, 0.1);
        this.runningAction = cc.repeatForever(new cc.Animate(animation));
        /*this.sprite = new cc.Sprite("#runner0.png");
        this.sprite.attr({x: 80, y: 85});
        this.sprite.runAction(this.runningAction);
        this.spriteSheet.addChild(this.sprite);*/

        this.sprite =new cc.PhysicsSprite("#runner0.png");
        var contentSize = this.sprite.getContentSize();

        this.body = new cp.Body(1, cp.momentForBox(1, contentSize.width, contentSize.height));
        this.body.p = cc.p(g_runnerStartX, g_groundHight + contentSize.height / 2);
        this.body.applyImpulse(cp.v(150,0),cp.v(0,0));
        this.space.addBody(this.body);

        this.shape = new cp.BoxShape(this.body, contentSize.width - 14, contentSize.height);
        this.space.addShape(this.shape);

        this.sprite.setBody(this.body);
        this.sprite.runAction(this.runningAction);

        this.spriteSheet.addChild(this.sprite);

        this.scheduleUpdate();
    },
    getEyeX:function(){
        return this.sprite.getPositionX()- g_runnerStartX;
    }
});