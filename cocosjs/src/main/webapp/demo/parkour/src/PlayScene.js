/**
 * Created by landy on 15/3/20.
 */
var PlayScene = cc.Scene.extend({
    onEnter: function () {
        this._super();
        this.addChild(new BackgroundLayer());
        this.addChild(new AnimationLayer());
        this.addChild(new StatusLayer());
    }
});