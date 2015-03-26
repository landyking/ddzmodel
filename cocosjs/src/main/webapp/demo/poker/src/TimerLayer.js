/**
 * Created by landy on 15/3/26.
 */
var TimerLayer = cc.Layer.extend({
    timeout: 30,


    ctor: function () {
        this._super();

        var size = cc.winSize;

        //var sprite = Global.createSpriteForCard(33);
        //sprite.setPosition(size.width / 2, size.height / 2);
        //this.addChild(sprite);

        this.timeoutLabel = new cc.LabelTTF("");
        this.timeoutLabel.setVisible(false);
        this.addChild(this.timeoutLabel);

        //this.startTimer("center", 30);

        return true;
    },
    startTimer: function (location, len) {
        this.cancelTimer();
        this.timeout = len;
        this.timeoutLabel.setVisible(true);
        if ("center" == location) {
            this.timeoutLabel.setPosition(cc.winSize.width / 2, Global.unitHeight * 1.5);
        } else if ("left" == location) {
            this.timeoutLabel.setPosition(Global.unitHeight * 1.5, cc.winSize.height / 2);
        } else if ("right" == location) {
            this.timeoutLabel.setPosition(cc.winSize.width - Global.unitHeight * 1.5, cc.winSize.height / 2);
        }
        this.schedule(this.updateTimeout, 1);
    },
    updateTimeout: function () {
        this.timeoutLabel.setString(this.timeout + "");
        this.timeout--;
        if (0 == this.timeout) {
            console.log("timeout!!!!!!");
            this.cancelTimer();
        }
    },
    cancelTimer: function () {
        this.timeoutLabel.setVisible(false);
        this.unschedule(this.updateTimeout);
    }
});